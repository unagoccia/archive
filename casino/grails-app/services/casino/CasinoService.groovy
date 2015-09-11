package casino

import grails.transaction.Transactional

@Transactional
class CasinoService {
    CasinoProperties casinoProperties = new CasinoProperties()

    def bet() {
      println "bet"
      String cmd = "cmd /c C:/casino/UWSC/uwsc48e/UWSC.exe C:/casino/UWSC/script/spinAndResult.uws"
      Process p = cmd.execute()
      p.waitFor();
    }

    def spin() {
      println "spin"
      String space = " "
      String cmd = "cmd /c " + casinoProperties.get(casinoProperties.UWSC_EXE) + space + casinoProperties.get(casinoProperties.UWSC_SCRIPT_DIR) + "spinAndResult.uws" + space + casinoProperties.get(casinoProperties.SPIN_RESULT_FILE)
      Process p = cmd.execute()
      p.waitFor();

      Properties spinResult = new Properties()
      File spinResultFile = new File(casinoProperties.get(casinoProperties.SPIN_RESULT_FILE))
      def dis = spinResultFile.newDataInputStream()
      spinResult.load(dis)
      def result = spinResult.getProperty('result')
      dis.close()
      return result
    }
}
