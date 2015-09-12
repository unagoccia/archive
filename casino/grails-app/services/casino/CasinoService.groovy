package casino

import grails.transaction.Transactional
import grails.converters.*

@Transactional
class CasinoService {
    CasinoProperties casinoProperties = new CasinoProperties()
    HashMap<String,Integer> recentSpinRecordMap = new HashMap<String,Integer>()
    List<HashMap> betList = new ArrayList<HashMap>();

    def reset() {
      betList = new ArrayList<HashMap>();
      for(int i=0; i<=36; i++) {
        HashMap<String,String> betObjMap = new HashMap<String,String>()
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

      if(!betList) {
        reset()
      }
      for(HashMap map : betList){
        def spinNum
        String mapNum =  map.get("num")
        if(mapNum.equals(spinResultNum)) {
          def recentHit = map.get("spinNum") + 1
          def spinResult = new SpinResult(hitNumber: spinResultNum, recentHit: recentHit)
          spinResult.save(flush: true)
          spinNum = 0
        } else {
          spinNum = map.get("spinNum") + 1
        }
        map.put("spinNum", spinNum)
      }

      def spinResultList = SpinResult.list(sort:"id", order:"desc", max:5)

      def result = [
        spinResultNum: spinResultNum,
        betList: betList,
        spinResultList: spinResultList
      ]

      return result
    }
}
