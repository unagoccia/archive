package casino

import grails.converters.*

class CasinoController {

  def casinoService

  def index() {
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

}
