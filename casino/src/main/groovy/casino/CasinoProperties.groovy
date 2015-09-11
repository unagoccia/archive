
package casino

class CasinoProperties {
  Properties casinoProps = new Properties()
  File propsFile = new File('C:/Casino/conf/casino.properties')

  String UWSC_EXE = "uwscEXE"
  String UWSC_SCRIPT_DIR = "uwscScriptDir"
  String SPIN_RESULT_FILE = "spinResultFile"

  def get(name) {
    casinoProps.load(propsFile.newDataInputStream())
    def prp

    switch (name) {
      case UWSC_EXE:
        prp = casinoProps.getProperty('uwsc.exe.path')
        break;
      case UWSC_SCRIPT_DIR:
        prp = casinoProps.getProperty('uwsc.script.dir')
        break;
      case SPIN_RESULT_FILE:
        prp = casinoProps.getProperty('spin.result.file')
        break;
    }
    return prp
  }
}
