package casino

import grails.transaction.Transactional
import grails.converters.*
import java.util.concurrent.TimeUnit

@Transactional
class CasinoService {
    CasinoProperties casinoProperties = new CasinoProperties()
    HashMap<String,Integer> recentSpinRecordMap = new HashMap<String,Integer>()
    List<HashMap> betList = new ArrayList<HashMap>()
    HashMap<String, String> loseMap = new HashMap<String, String>() //loosMap(key:number, value:betID)

    boolean isNotBeBetNewly = false

    // ベットステータスを変える際の閾値
    // final def STATUS_CHANGED_TO_READY_NUM = 100
    final int STATUS_CHANGED_TO_BET_NUM = 200
    final int STATUS_CHANGED_TO_WAIT_NUM = 100
    // final int STATUS_CHANGED_TO_WAIT_IN_A_ROW = 10;
    final int IN_A_ROW_THRESHOLD = 100;
    // final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM_FROM = 10
    // final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM_TO = 80
    final int STATUS_CHANGED_TO_LOSE_NUM = 200
    final int BET_AGAIN_MAX_SPIN = 200;

    //WATCH(最大賭け金) 最終的には削除予定
    BigDecimal max_stakes = 0

    def isNotBeBetNewly() {
      return isNotBeBetNewly
    }

    def notBeBetNewlyChange() {
      isNotBeBetNewly = !isNotBeBetNewly
      return isNotBeBetNewly
    }

    def reset() {
      betList = new ArrayList<HashMap>()
      for(int i=0; i<=36; i++) {
        HashMap betObjMap = new HashMap()
        betObjMap.put("id", null)
        betObjMap.put("num", i)
        betObjMap.put("status", "-")
        betObjMap.put("currentSpinNum", 0)
        betObjMap.put("nextStakes", "-")
        betObjMap.put("spinNumFromFirstBet",0)
        betObjMap.put("inarowCount", 0)
        betObjMap.put("waited", false)
        betList.add(betObjMap)
      }
      return betList
    }

    def bet() {
      println "bet"
      String cmd = "cmd /c C:/casino/UWSC/uwsc48e/UWSC.exe C:/casino/UWSC/script/spinAndResult.uws"
      Process p = cmd.execute()
      p.waitFor();
    }

    def spin() {
      String space = " "
      String cmd = "cmd /c " + casinoProperties.get(casinoProperties.UWSC_EXE) + space + casinoProperties.get(casinoProperties.UWSC_SCRIPT_DIR) + "spinAndResult.uws" + space + casinoProperties.get(casinoProperties.SPIN_RESULT_FILE) + space + casinoProperties.get(casinoProperties.UWSC_IMAGE_DIR)
      Process p = cmd.execute()

      def result
      boolean isSuccess = p.waitFor(30, TimeUnit.SECONDS); //10秒でタイムアウト
    	if (isSuccess) {
        Properties spinResultProp = new Properties()
        File spinResultFile = new File(casinoProperties.get(casinoProperties.SPIN_RESULT_FILE))
        def dis = spinResultFile.newDataInputStream()
        spinResultProp.load(dis)
        def spinResultNum = spinResultProp.getProperty('result')
        dis.close()

        // ベットリストがnullだったらリセット
        if(!betList) {
          reset()
        }

//賭け金合計　最終的には削除予定
BigDecimal stakes_sum = 0

        // ベットリスト更新
        for(HashMap betData : betList){
          def bet
          def profit
          def stakes
          def currentSpinNum
          def spinResult
          String betDataNum = betData.get("num")
          String currentStatus = betData.get("status")
          int currentInarow = betData.get("inarowCount")

          // スピン結果の記録
          if(betDataNum.equals(spinResultNum)) {
            def recentHit = betData.get("currentSpinNum") + 1
            spinResult = new SpinResult(hitNumber: spinResultNum, recentHit: recentHit, enabled: true)
            spinResult.save()

            // 負けの回転結果を記録
            if(loseMap.containsKey(spinResultNum)) {
              bet = Bet.get(loseMap.get(spinResultNum))
              // println "LOSE:" + bet
              // println spinResult
              bet.spinResult = spinResult
              bet.save()
              loseMap.remove(spinResultNum)
            }
          }

          //現在の回転数の更新
          if(betDataNum.equals(spinResultNum)) {
            currentSpinNum = 0
          } else {
            currentSpinNum = betData.get("currentSpinNum") + 1
          }
          betData.put("currentSpinNum", currentSpinNum)

          // if (!loseMap.isEmpty()) {
          //   println "負けの回転結果を記録"
          //   println loseMap
          //   println spinResultNum
          //   println spinResult
          //   println loseMap.containsKey(spinResultNum)
          // }

          // ベット判定
          if(betDataNum.equals(spinResultNum)) {
            if(currentStatus.equals(Bet.STATUS_BET)) {
              bet = Bet.get(betData.get("id"))
              bet.status = Bet.STATUS_WIN
              bet.spinResult = spinResult
              def recentHit = spinResult.recentHit

              if(betData.get("waited")) {
                recentHit = recentHit + STATUS_CHANGED_TO_WAIT_NUM
                betData.put("waited", false)
              }

              stakes = Stakes.withCriteria {
                eq('spinNum', recentHit)
              }

              profit = bet.profit
              if(!profit) {
                profit = new Profit()
              }
              profit.profit = stakes[0].profit
              profit.enabled = true
              profit.save()
              bet.profit = profit
              bet.save()

              //連チャン数の更新
              if(recentHit < IN_A_ROW_THRESHOLD) {
                currentInarow++
              } else {
                currentInarow = 0
              }
              // println "Win:" + currentInarow
              betData.put("inarowCount", currentInarow)

              // 再ベット判定
              int spinNumFromFirstBet = betData.get("spinNumFromFirstBet") + recentHit
              if(!isNotBeBetNewly && spinNumFromFirstBet <= BET_AGAIN_MAX_SPIN) {
                bet = new Bet(betNumber: betDataNum, status: Bet.STATUS_BET)
                bet.save()

                betData.put("id", bet.id)
                betData.put("status", Bet.STATUS_BET)
                betData.put("spinNumFromFirstBet", spinNumFromFirstBet)
                currentStatus = Bet.STATUS_BET
              } else {
                betData.put("id", null)
                betData.put("status", "-")
                betData.put("spinNumFromFirstBet", 0)
                currentStatus = "-"
              }
            } else if(!isNotBeBetNewly && currentStatus.equals("-") && spinResult.recentHit >= STATUS_CHANGED_TO_BET_NUM) {
              bet = new Bet(betNumber: betDataNum, status: Bet.STATUS_BET)
              bet.save()

              betData.put("id", bet.id)
              betData.put("status", Bet.STATUS_BET)
              betData.put("spinNumFromFirstBet", 0)
              currentStatus = Bet.STATUS_BET
            } else if(currentStatus.equals(Bet.STATUS_WAIT)) {
              bet = Bet.get(betData.get("id"))
              bet.status = Bet.STATUS_BET
              betData.put("status", Bet.STATUS_BET)
              currentStatus = Bet.STATUS_BET
            }
            // } else if(currentStatus.equals(Bet.STATUS_WAIT) && spinResult.recentHit >= STATUS_CHANGED_TO_BET_FROM_WAIT_NUM_FROM && spinResult.recentHit <= STATUS_CHANGED_TO_BET_FROM_WAIT_NUM_TO) {
            //   bet = Bet.get(betData.get("id"))
            //   bet.status = Bet.STATUS_BET
            //   betData.put("status", Bet.STATUS_BET)
            // }
          } else {

            if(betData.get("waited")) {
              currentSpinNum = currentSpinNum + STATUS_CHANGED_TO_WAIT_NUM
            }

            if(currentStatus.equals(Bet.STATUS_BET)) {
              if(currentSpinNum == STATUS_CHANGED_TO_LOSE_NUM) {
                bet = Bet.get(betData.get("id"))
                bet.status = Bet.STATUS_LOSE
                stakes = Stakes.withCriteria {
                  eq('spinNum', currentSpinNum)
                }
                profit = bet.profit
                profit.profit = -stakes[0].stakesTotal
                profit.enabled = true
                profit.save()
                bet.profit = profit
                bet.save()

                def loseBetID = betData.get("id")
                loseMap.put(betDataNum, loseBetID)
                betData.put("id", null)
                betData.put("status", "-")
                betData.put("spinNumFromFirstBet", 0)
                betData.put("nextStakes", "-")
                betData.put("inarowCount", 0)
                currentStatus = Bet.STATUS_LOSE
                betData.put("waited", false)
              } else {
                bet = Bet.get(betData.get("id"))

                if(currentSpinNum == STATUS_CHANGED_TO_WAIT_NUM) {
                  bet.status = Bet.STATUS_WAIT
                  betData.put("status", Bet.STATUS_WAIT)
                  betData.put("waited", true)
                  currentStatus = Bet.STATUS_WAIT
                }

                stakes = Stakes.withCriteria {
                  eq('spinNum', currentSpinNum)
                }
                profit = bet.profit
                if(!profit) {
                  profit = new Profit()
                }
                profit.profit = -stakes[0].stakesTotal
                profit.save()
                bet.profit = profit
                bet.save()
              }
            }
          //   // } else if(currentStatus.equals(Bet.STATUS_BET) && currentSpinNum == STATUS_CHANGED_TO_WAIT_NUM && currentInarow >= STATUS_CHANGED_TO_WAIT_IN_A_ROW) {
          //   } else if(currentStatus.equals(Bet.STATUS_BET) && currentSpinNum == STATUS_CHANGED_TO_WAIT_NUM) {
          //     println "change Wait:" + currentInarow
          //     bet = Bet.get(betData.get("id"))
          //     bet.status = Bet.STATUS_WAIT
          //     betData.put("status", Bet.STATUS_WAIT)
          //     betData.put("waited", true)
          //   }
          }


  // ============================old bet judge=======================================//
          // def bet
          // String currentStatus = betData.get("status")
          // if(betDataNum.equals(spinResultNum)) {
          //   if(currentStatus.equals(Bet.STATUS_READY)) {
          //    bet = Bet.get(betData.get("id"))
          //    bet.status = Bet.STATUS_NO_BET
          //    bet.spinResult = spinResult
          //    bet.save()
          //    betData.put("status", "-")
          //  } else if(currentStatus.equals(Bet.STATUS_BET)) {
          //     bet = Bet.get(betData.get("id"))
          //     bet.status = Bet.STATUS_WIN
          //     bet.spinResult = spinResult
          //     def stakes = Stakes.withCriteria {
          //       eq('spinNum', spinResult.recentHit)
          //     }
          //     def profit = new Profit(profit: stakes[0].profit)
          //     profit.save()
          //     bet.profit = profit
          //     bet.save()
          //     betData.put("status", "-")
          //   }else if(currentStatus.equals("-") && spinResult.recentHit >= STATUS_CHANGED_TO_READY_NUM) {
          //     bet = new Bet(betNumber: betDataNum, status: Bet.STATUS_READY)
          //     bet.save()
          //     betData.put("id", bet.id)
          //     betData.put("status", Bet.STATUS_READY)
          //   // } else if(currentStatus.equals(Bet.STATUS_WAIT) && spinResult.recentHit <= STATUS_CHANGED_TO_BET_FROM_WAIT_NUM) {
          //   //   bet = Bet.get(betData.get("id"))
          //   //   bet.status = Bet.STATUS_BET
          //   //   bet.save()
          //   //   betData.put("currentSpinNum", STATUS_CHANGED_TO_WAIT_NUM + 1) //回転数はWAITにした際の回転数から再開
          //   //   betData.put("status", Bet.STATUS_BET)
          //   }
          // } else {
          //   if(currentStatus.equals(Bet.STATUS_READY) && currentSpinNum == STATUS_CHANGED_TO_BET_NUM) {
          //     bet = Bet.get(betData.get("id"))
          //     bet.status = Bet.STATUS_BET
          //     bet.save()
          //     betData.put("status", Bet.STATUS_BET)
          //     // } else if(currentStatus.equals(Bet.STATUS_BET) && currentSpinNum == STATUS_CHANGED_TO_WAIT_NUM) {
          //     //   bet = Bet.get(betData.get("id"))
          //     //   bet.status = Bet.STATUS_WAIT
          //     //   bet.save()
          //     //   betData.put("status", Bet.STATUS_WAIT)
          //     } else if(currentStatus.equals(Bet.STATUS_BET) && currentSpinNum == STATUS_CHANGED_TO_LOSE_NUM) {
          //       bet = Bet.get(betData.get("id"))
          //       bet.status = Bet.STATUS_LOSE
          //       def stakes = Stakes.withCriteria {
          //         eq('spinNum', currentSpinNum)
          //       }
          //       def profit = new Profit(profit: -stakes[0].stakesTotal)
          //       profit.save()
          //       bet.profit
          //       bet.save()
          //       betData.put("status", "-")
          //       loseMap.put(betDataNum, betData.get("id"))
          //     }
          // }
  // ============================old=======================================//

          // 次ベット金額の更新
          if(currentStatus.equals(Bet.STATUS_BET)) {
            def nextSpinNum = betData.get("currentSpinNum")+1
            if(betDataNum.equals(spinResultNum)) {
              betData.put("nextStakes", "-")
            } else {
              stakes = Stakes.withCriteria {
                eq('spinNum', nextSpinNum)
              }
// DEBUG
if(!stakes[0]) {
  println betDataNum
}
              betData.put("nextStakes", stakes[0].stakes)
              stakes_sum += stakes[0].stakesTotal
            }
          } else {
            betData.put("nextStakes", "-")
          }
        }

//最大賭け金更新　最終的には削除予定
if(max_stakes < stakes_sum) {
  max_stakes = stakes_sum
  if(max_stakes > 800) {
    println "max_stakes: \$" + max_stakes
  }
}


        def spinResultList = getSpinResultList()
        result = [
          spinResultNum: spinResultNum,
          betList: betList,
          spinResultList: spinResultList
        ]
      } else {
        println "Faild: spinAndResult.uws"
        p.destroy(); // プロセスを強制終了
        // cmd = 'cmd /c taskkill.exe /f /fi "status eq not responding"'
        // p = cmd.execute()
        // p.waitFor(10, TimeUnit.SECONDS);
        result = [
          errorMessage: "Faild: spinAndResult.uws"
        ]
      }
      return result
    }

    def getBetList() {
      // ベットリストがnullだったらリセット
      if(!betList) {
        reset()
      }
      return betList
    }

    def getSpinResultList() {
      def spinResultList = SpinResult.list(sort:"id", order:"desc", max:5)
      return spinResultList
    }

    def getProfitList() {
      def bets = Bet.withCriteria {
        isNotNull("profit")
        profit {
          eq "enabled", true
        }
        maxResults(5)
        order("id", "desc")
      }

      def result = new ArrayList<HashMap>()
      for (bet in bets) {
        def tmp = new HashMap()
        tmp.put("id", bet.profit.id)
        tmp.put("date", bet.profit.dateCreated.format("MM/dd"))
        tmp.put("number", bet.betNumber)
        tmp.put("profit", bet.profit.profit)
        if(bet.spinResult) {
          tmp.put("recentHit", bet.spinResult.recentHit)
        } else {
          tmp.put("recentHit", "-")
        }
        result.add(tmp)
      }
      return result
    }

    def getMonthlyProfitList() {
      def monthlyProfitList = Profit.withCriteria {
        eq "enabled", true
        projections {
          sum('profit')
          groupProperty('year')
          groupProperty('month')
        }
      }

      def result = new ArrayList<HashMap>()
      for (monthlyProfit in monthlyProfitList) {
        def tmp = new HashMap()
        tmp.put("month", monthlyProfit[1] + "/" + monthlyProfit[2])
        tmp.put("mProfit", monthlyProfit[0])
        result.add(tmp)
      }
      return result
    }

}
