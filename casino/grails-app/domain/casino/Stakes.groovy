package casino

class Stakes {

  /**
   * 回転数
   */
  int spinNum

  /**
   * 賭け金
   */
  BigDecimal  stakes

  /**
   * 賭け金(累計)
   */
  BigDecimal stakesTotal

  /**
   * 利益
   */
  BigDecimal profit

  static constraints = {
      spinNum nullable: false, blank: false, unique: true
      stakes nullable: false, blank: false, scale: 2
      stakesTotal nullable: true, blank: true, scale: 2
      profit nullable: true, blank: true, scale: 2
  }
}
