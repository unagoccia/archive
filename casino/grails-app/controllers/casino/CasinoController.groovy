package casino

import grails.converters.*

class CasinoController {

  def casinoService
  Boolean autoMode = false

  def index() {
  }

  def autoModeChange() {
    autoMode = !autoMode
    def result = [
      result: autoMode
    ]

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
    }
  }

}
