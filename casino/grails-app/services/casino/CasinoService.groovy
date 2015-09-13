package casino

import grails.transaction.Transactional
import grails.converters.*

@Transactional
class CasinoService {
    CasinoProperties casinoProperties = new CasinoProperties()
    HashMap<String,Integer> recentSpinRecordMap = new HashMap<String,Integer>()
    List<HashMap> betList = new ArrayList<HashMap>()

    // ベットステータスを変える際の閾値
    final def STATUS_CHANGED_TO_READY_NUM = 100
    final int STATUS_CHANGED_TO_BET_NUM = 30
    final int STATUS_CHANGED_TO_WAIT_NUM = 100
    final int STATUS_CHANGED_TO_BET_FROM_WAIT_NUM = 100
    final int STATUS_CHANGED_TO_LOSE_NUM = 166

    def reset() {
      betList = new ArrayList<HashMap>();
      for(int i=0; i<=36; i++) {
        HashMap<String,String> betObjMap = new HashMap<String,String>()
        betObjMap.put("id", null)
        betObjMap.put("num", i)
        betObjMap.put("status", "-")
        betObjMap.put("spinNum", 0)
        betObjMap.put("stakes", "-")
        betObjMap.put("recentHit", "-")
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
      Bet bet = new Bet()
      bet.betNumber = 1
      bet.status = Bet.STATUS_READY
      println bet.save(flush: true)
      String space = " "
      String cmd = "cmd /c " + casinoProperties.get(casinoProperties.UWSC_EXE) + space + casinoProperties.get(casinoProperties.UWSC_SCRIPT_DIR) + "spinAndResult.uws" + space + casinoProperties.get(casinoProperties.SPIN_RESULT_FILE)
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
      for(HashMap map : betList){
        //現在の回転数 & 当たりまでの回転の更新
        def spinNum
        String mapNum =  map.get("num")
        def spinResult
        if(mapNum.equals(spinResultNum)) {
          def recentHit = map.get("spinNum") + 1
          spinResult = new SpinResult(hitNumber: spinResultNum, recentHit: recentHit)
          spinResult.save(flush: true)
          spinNum = 0
        } else {
          spinNum = map.get("spinNum") + 1
        }
        map.put("spinNum", spinNum)

        // ベット判定
        String currentStatus = map.get("status")
        if(mapNum.equals(spinResultNum)) {
          if(currentStatus.equals(Bet.STATUS_READY)) {
           bet = Bet.get(map.get(id))
           bet.status = Bet.STATUS_NO_BET
           bet.spinResult = spinResult
           bet.save(flush: true)
           map.put("status", "-")
         } else if(currentStatus.equals(Bet.STATUS_BET)) {
            bet = Bet.get(map.get(id))
            bet.status = Bet.STATUS_WIN
            bet.spinResult = spinResult
            // TODO 利益入力
            // bet.profit =
            bet.save(flush: true)
            map.put("status", "-")
          }else if(currentStatus.equals("-") && spinResult.recentHit >= STATUS_CHANGED_TO_READY_NUM) {
            bet = new Bet(betNumber: map.get("num"), status: Bet.STATUS_READY)
            bet.save(flush: true)
            map.put("id", bet.id)
            map.put("status", Bet.STATUS_READY)
          } else if(currentStatus.equals(Bet.STATUS_WAIT) && spinResult.recentHit <= STATUS_CHANGED_TO_BET_FROM_WAIT_NUM) {
            bet = Bet.get(map.get(id))
            bet.status = Bet.STATUS_BET
            bet.save(flush: true)
            map.put("status", Bet.STATUS_BET)
          }
        } else {
          if(currentStatus.equals(Bet.STATUS_READY) && spinNum >= STATUS_CHANGED_TO_BET_NUM) {
            bet = Bet.get(map.get("id"))
            bet.status = Bet.STATUS_BET
            bet.save(flush: true)
            map.put("status", Bet.STATUS_BET)
          } else if(currentStatus.equals(Bet.STATUS_BET) && spinNum >= STATUS_CHANGED_TO_WAIT_NUM) {
            bet = Bet.get(map.get(id))
            bet.status = Bet.STATUS_WAIT
            bet.save(flush: true)
            map.put("status", Bet.STATUS_WAIT)
          }
        }
      }

      def spinResultList = SpinResult.list(sort:"id", order:"desc", max:5)

      def result = [
        spinResultNum: spinResultNum,
        betList: betList,
        spinResultList: spinResultList
      ]

      return result
    }

    def updateBetStatus() {

    }

}
