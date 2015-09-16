package casino

import grails.transaction.Transactional
import grails.converters.*
import static grails.async.Promises.*

@Transactional
class CasinoService {
    CasinoProperties casinoProperties = new CasinoProperties()
    HashMap<String,Integer> recentSpinRecordMap = new HashMap<String,Integer>()
    List<HashMap> betList = new ArrayList<HashMap>()
    HashMap<String, String> loseMap = new HashMap<String, String>() //loosMap(key:number, value:betID)

    // ベットステータスを変える際の閾値
    // final def STATUS_CHANGED_TO_READY_NUM = 100
    // final int STATUS_CHANGED_TO_BET_NUM = 30
    final int STATUS_CHANGED_TO_BET_NUM = 200
    // final int STATUS_CHANGED_TO_WAIT_NUM = 100
    // final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM = 100
    final int STATUS_CHANGED_TO_LOSE_NUM = 180
    final int BET_AGAIN_MAX_SPIN = 300;

    def reset() {
      betList = new ArrayList<HashMap>()
      for(int i=0; i<=36; i++) {
        HashMap<String,String> betObjMap = new HashMap<String,String>()
        betObjMap.put("id", null)
        betObjMap.put("num", i)
        betObjMap.put("status", "-")
        betObjMap.put("currentSpinNum", 0)
        betObjMap.put("nextStakes", "-")
        betObjMap.put("spinNumFromFirstBet",0)
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
      p.waitFor();

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

      // ベットリスト更新
      for(HashMap betData : betList){
        //現在の回転数 & 当たりまでの回転の更新
        def currentSpinNum
        def spinResult
        String betDataNum = betData.get("num")
        if(betDataNum.equals(spinResultNum)) {
          def recentHit = betData.get("currentSpinNum") + 1
          spinResult = new SpinResult(hitNumber: spinResultNum, recentHit: recentHit)
          spinResult.save()
          currentSpinNum = 0
        } else {
          currentSpinNum = betData.get("currentSpinNum") + 1
        }
        betData.put("currentSpinNum", currentSpinNum)

        def bet
        // 負けの回転結果を記録
        if(loseMap.containsKey(spinResultNum)) {
          bet = Bet.get(loseMap.get(spinResultNum))
          bet.spinResult = spinResult
          bet.save()
          loseMap.remove(spinResultNum)
        }

        // ベット判定
        String currentStatus = betData.get("status")
        if(betDataNum.equals(spinResultNum)) {
          int spinNumFromFirstBet = betData.get("spinNumFromFirstBet")
          if(currentStatus.equals(Bet.STATUS_BET)) {
            bet = Bet.get(betData.get("id"))
            bet.status = Bet.STATUS_WIN
            bet.spinResult = spinResult
            def stakes = Stakes.withCriteria {
              eq('spinNum', spinResult.recentHit)
            }
            def profit = new Profit(profit: stakes[0].profit)
            profit.save()
            bet.profit = profit
            bet.save()

            spinNumFromFirstBet = spinNumFromFirstBet + spinResult.recentHit

            // 再ベット判定
            if(spinNumFromFirstBet <= BET_AGAIN_MAX_SPIN) {
              bet = new Bet(betNumber: betDataNum, status: Bet.STATUS_BET)
              bet.save()
              betData.put("id", bet.id)
              betData.put("status", Bet.STATUS_BET)
              spinNumFromFirstBet++
              betData.put("spinNumFromFirstBet", spinNumFromFirstBet)
            } else {
              betData.put("id", null)
              betData.put("status", "-")
              betData.put("spinNumFromFirstBet", 0)
            }
          } else if(currentStatus.equals("-") && spinResult.recentHit >= STATUS_CHANGED_TO_BET_NUM) {
            bet = new Bet(betNumber: betDataNum, status: Bet.STATUS_BET)
            bet.save()
            betData.put("id", bet.id)
            betData.put("status", Bet.STATUS_BET)
            spinNumFromFirstBet++
            betData.put("spinNumFromFirstBet", spinNumFromFirstBet)
          }
        } else {
          if(currentStatus.equals(Bet.STATUS_BET) && currentSpinNum == STATUS_CHANGED_TO_LOSE_NUM) {
            bet = Bet.get(betData.get("id"))
            bet.status = Bet.STATUS_LOSE
            bet.spinResult = spinResult
            def stakes = Stakes.withCriteria {
              eq('spinNum', currentSpinNum)
            }
            def profit = new Profit(profit: -stakes[0].stakesTotal)
            profit.save()
            bet.profit = profit
            bet.save()
            def loseBetID = betData.get("id")
            loseMap.put(betDataNum, loseBetID)
            betData.put("id", null)
            betData.put("status", "-")
            betData.put("spinNumFromFirstBet", 0)
          }
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
            def stakes = Stakes.withCriteria {
              eq('spinNum', nextSpinNum)
            }
            betData.put("nextStakes", stakes[0].stakes)
          }
        }
      }


      def spinResultList = getSpinResultList()
      def result = [
        spinResultNum: spinResultNum,
        betList: betList,
        spinResultList: spinResultList
      ]

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
        maxResults(5)
        order("id", "desc")
      }

      def result = new ArrayList<HashMap>()
      for (bet in bets) {
        def tmp = new HashMap()
        tmp.put("id", bet.profit.id)
        tmp.put("date", bet.profit.dateCreated.format("MM/dd"))
        tmp.put("number", bet.betNumber)
        tmp.put("recentHit", bet.spinResult.recentHit)
        tmp.put("profit", bet.profit.profit)
        result.add(tmp)
      }
      return result
    }

    def getMonthlyProfitList() {
      def monthlyProfitList = Profit.withCriteria {
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
