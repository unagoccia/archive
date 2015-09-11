package casino

import grails.converters.*

class CasinoController {

  def casinoService

  def index() {
  }

  def bet() {
    casinoService.bet()
    render "bet"
  }

  def spin() {
    def result = [
      result: casinoService.spin()
    ]
    render result as JSON
  }

}
