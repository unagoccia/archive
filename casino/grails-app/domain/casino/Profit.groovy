package casino

class Profit {

    /**
     * 登録日時
     */
    Date dateCreated

    String year

    String month

    String week

    /**
     * 利益
     */
    BigDecimal  profit

    static constraints = {
        profit nullable: false, blank: false, scale: 2
    }

    static mapping = {
      week  formula: 'week(date_created)'
      month formula: 'month(date_created)'
      year  formula: 'year(date_created)'
    }
}
