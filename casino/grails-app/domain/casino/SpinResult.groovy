package casino

class SpinResult {

  /**
   * ヒットした数
   */
  int hitNumber

  /**
   * 前回の当たりからの回転数
   */
  int recentHit

  /**
   * 登録日時
   */
  Date dateCreated

  String year

  String month

  String week

  String day

  static constraints = {
      hitNumber nullable: false, blank: false
      recentHit nullable: false, blank: false
  }

  static mapping = {
    day  formula: 'day(date_created)'
    week  formula: 'week(date_created)'
    month formula: 'month(date_created)'
    year  formula: 'year(date_created)'
  }
}
