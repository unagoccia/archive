package casino

import grails.converters.*

class CasinoController {

  def casinoService
  Boolean autoMode = false

  def index() {
  }

  def isNotBeBetNewly() {
    def notBeBetNewly = casinoService.isNotBeBetNewly()
    def result = [
      result: notBeBetNewly
    ]
    render result as JSON
  }

  def notBeBetNewlyChange() {
    def notBeBetNewly = casinoService.notBeBetNewlyChange()
    def result = [
      result: notBeBetNewly
    ]
    println "notBeBetNewly:" + notBeBetNewly
    render result as JSON
  }

  def getAutoMode() {
    def result = [
      result: autoMode
    ]
    render result as JSON
  }

  def autoModeChange() {
    autoMode = !autoMode
    def result = [
      result: autoMode
    ]
    println "chaneMode:" + autoMode
    render result as JSON
  }

  def reset() {
    def result = casinoService.reset()
    render result as JSON
  }

  def bet() {
    casinoService.bet()
    render "bet"
  }

  def spin() {
    def result = casinoService.spin()
    render result as JSON
  }

  def auto() {
    render status:200

    while(autoMode) {
      casinoService.spin()
      if(casinoService.isTargetIncomeAchievementOfToday()) {
        autoMode = false
      }
    }
  }

  def getBetList() {
    def result = casinoService.getBetList()
    render result as JSON
  }

  def getSpinResultList() {
    def result = casinoService.getSpinResultList()
    render result as JSON
  }

  def getProfitList() {
    def result = casinoService.getProfitList()
    render result as JSON
  }

  def getMonthlyProfitList() {
    def result = casinoService.getMonthlyProfitList()
    render result as JSON
  }

}
