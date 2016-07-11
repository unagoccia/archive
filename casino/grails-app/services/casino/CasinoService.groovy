package casino

import grails.transaction.Transactional
import grails.converters.*
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat

@Transactional
class CasinoService {
    CasinoProperties casinoProperties = new CasinoProperties()
    HashMap<String,Integer> recentSpinRecordMap = new HashMap<String,Integer>()
    List<HashMap> betList = new ArrayList<HashMap>()
    HashMap<String, String> loseMap = new HashMap<String, String>() //loosMap(key:number, value:betID)

    boolean isNotBeBetNewly = false
    boolean isSimulation = false

    // ベットステータスを変える際の閾値
    // final def STATUS_CHANGED_TO_READY_NUM = 100
    final int STATUS_CHANGED_TO_BET_NUM = 200
    // final int STATUS_CHANGED_TO_WAIT_NUM = 100
    // final int STATUS_CHANGED_TO_WAIT_IN_A_ROW = 10;
    // final int IN_A_ROW_THRESHOLD = 100;
    // final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM_FROM = 10
    // final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM_TO = 80
    // final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM = 100
    final int STATUS_CHANGED_TO_LOSE_NUM = 200
    final int BET_AGAIN_MAX_SPIN = 200
    final int TARGET_AMOUNT = 2000
    final int LIMIT_PROFIT_OF_ONECE = 400
    final String ORDER_DATE = "lastUpdated"
    final String ORDER_PROFIT = "profit"
    final String ORDER_RECENT_HIT = "recentHit"
    int baseProfit = 0

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
        betObjMap.put("status", Bet.STATUS_NO_BET)
        betObjMap.put("currentSpinNum", 0)
        betObjMap.put("nextStakes", "-")
        betObjMap.put("spinNumFromFirstBet",0)
        betList.add(betObjMap)
      }
      return betList
    }

    def bet() {
      println "bet"
      String cmd = "cmd /c " + casinoProperties.get(casinoProperties.UWSC_EXE) + space + casinoProperties.get(casinoProperties.UWSC_SCRIPT_DIR) + "spinAndResult.uws" + space + casinoProperties.get(casinoProperties.SPIN_RESULT_FILE) + space + casinoProperties.get(casinoProperties.UWSC_IMAGE_DIR)
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

        // TODO リファクタリング
        // if(isNotBeBetNewly && !isBet()) {
        //   isNotBeBetNewly = false
        //   isSimulation = true
        // }

        // 設定金額に達したら一旦ベットしないようにする(isNotBeBetNewly=true)
        // 別途していたら負けてたタイミングで再度ベット開始
        def todayProfit = getTodayProfit()
        def profitAtOnce = todayProfit - baseProfit
        if(LIMIT_PROFIT_OF_ONECE <= profitAtOnce) {
            isNotBeBetNewly = true
            isSimulation = true
            baseProfit = getTodayProfit()
        }

        // ベットリスト更新
        for(HashMap betData : betList){
          def bet
          def profit
          def stakes
          def currentSpinNum
          def spinResult
          String betDataNum = betData.get("num")
          String currentStatus = betData.get("status")

          // スピン結果の記録
          if(betDataNum.equals(spinResultNum)) {
            def recentHit = betData.get("currentSpinNum") + 1
            spinResult = new SpinResult(hitNumber: spinResultNum, recentHit: recentHit)
            spinResult.save()

            // 負けの回転結果を記録
            if(loseMap.containsKey(spinResultNum)) {
              bet = Bet.get(loseMap.get(spinResultNum))

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

          // ベット判定
          if(betDataNum.equals(spinResultNum)) {
            if(currentStatus.equals(Bet.STATUS_BET)) {
              bet = Bet.get(betData.get("id"))
              bet.status = Bet.STATUS_WIN
              bet.spinResult = spinResult
              def recentHit = spinResult.recentHit

              stakes = Stakes.withCriteria {
                eq('spinNum', recentHit)
              }

              profit = bet.profit
              if(!profit) {
                profit = new Profit()
              }
              profit.profit = stakes[0].profit
              if(!isSimulation) {
                profit.enabled = true
              } else {
                profit.enabled = false
              }
              profit.save()
              bet.profit = profit
              bet.save()

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
                betData.put("status", Bet.STATUS_NO_BET)
                betData.put("spinNumFromFirstBet", 0)
                currentStatus = Bet.STATUS_NO_BET
              }
            } else if(!isNotBeBetNewly && currentStatus.equals(Bet.STATUS_NO_BET) && spinResult.recentHit >= STATUS_CHANGED_TO_BET_NUM) {
              bet = new Bet(betNumber: betDataNum, status: Bet.STATUS_BET)
              bet.save()

              betData.put("id", bet.id)
              betData.put("status", Bet.STATUS_BET)
              betData.put("spinNumFromFirstBet", 0)
              currentStatus = Bet.STATUS_BET
            }
          } else {
            if(currentStatus.equals(Bet.STATUS_BET)) {
              if(currentSpinNum == STATUS_CHANGED_TO_LOSE_NUM) {
                bet = Bet.get(betData.get("id"))
                bet.status = Bet.STATUS_LOSE
                stakes = Stakes.withCriteria {
                  eq('spinNum', currentSpinNum)
                }
                profit = bet.profit
                profit.profit = -stakes[0].stakesTotal
                if(!isSimulation) {
                  profit.enabled = true
                } else {
                  profit.enabled = false
                }
                profit.save()
                bet.profit = profit
                bet.save()

                def loseBetID = betData.get("id")
                loseMap.put(betDataNum, loseBetID)
                betData.put("id", null)
                betData.put("status", Bet.STATUS_NO_BET)
                betData.put("spinNumFromFirstBet", 0)
                betData.put("nextStakes", "-")
                betData.put("inarowCount", 0)
                currentStatus = Bet.STATUS_NO_BET
                // currentStatus = Bet.STATUS_LOSE
                // betData.put("waited", false)

                isNotBeBetNewly = false
                isSimulation = false
              } else {
                bet = Bet.get(betData.get("id"))

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
          }

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

    def getProfitList(reqOrder) {
      def bets = Bet.withCriteria {
        isNotNull("profit")
        profit {
          eq "enabled", true
        }
        maxResults(5)
        switch ("${reqOrder}") {
          case ORDER_DATE:
            profit {
              order("${reqOrder}", "desc")
            }
            break
          case ORDER_PROFIT:
            profit {
              order("${reqOrder}", "desc")
            }
            break
          case ORDER_RECENT_HIT:
            spinResult {
              order("${reqOrder}", "desc")
            }
            break
          default:
            order("${reqOrder}", "desc")
            break
        }
      }

      def result = new ArrayList<HashMap>()
      for (bet in bets) {
        def tmp = new HashMap()
        tmp.put("id", bet.profit.id)
        tmp.put("date", bet.profit.lastUpdated.format("MM/dd HH:mm:ss"))
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

    def getDaylyProfitList() {
      def daylyProfitList = Profit.withCriteria {
        eq "enabled", true
        projections {
          sum('profit')
          groupProperty('year')
          groupProperty('month')
          groupProperty('day')
        }
      }

      // def result = new ArrayList<HashMap>()
      def result = new HashMap()
      for (daylyProfit in daylyProfitList) {
        // def tmp = new HashMap()
        // tmp.put("day", daylyProfit[1] + "/" + daylyProfit[2] + "/" + daylyProfit[3])
        // tmp.put("dProfit", daylyProfit[0])
        // result.add(tmp)
        String day = daylyProfit[1] + "/" + daylyProfit[2] + "/" + daylyProfit[3]
        result.put(day, daylyProfit[0])
      }
      return result
    }

    def getTodayProfit() {
      SimpleDateFormat formatA = new SimpleDateFormat("yyyy/M/d");
      String formatDate = formatA.format(new Date());
      def todayProfit = getDaylyProfitList().get(formatDate)
      todayProfit = todayProfit ?: 0

      return todayProfit
    }

    def isTargetIncomeAchievementOfToday() {
      def todayProfit = getTodayProfit()
      if(todayProfit >= TARGET_AMOUNT) {
        return true
      }
      return false
    }

    def isBet() {
      for(HashMap betData : betList){
        String currentStatus = betData.get("status")
        if(!currentStatus.equals(Bet.STATUS_NO_BET)) {
          return true
        }
      }
      return false
    }
}
