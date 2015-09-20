package casino

class Profit {

  /**
   * 利益
   */
  BigDecimal  profit

  /**
   * 有効
   */
  boolean enabled

  /**
   * 登録日時
   */
  Date dateCreated

  String year

  String month

  String week

  String day

  static constraints = {
      profit nullable: false, blank: false, scale: 2
  }

  static mapping = {
    enabled defaultValue: "false"
    day  formula: 'day(date_created)'
    week  formula: 'week(date_created)'
    month formula: 'month(date_created)'
    year  formula: 'year(date_created)'
  }
}
