package casino

class Profit {

    /**
     * 登録日時
     */
    Date dateCreated

    /**
     * 利益
     */
    float profit

    static constraints = {
        profit nullable: false, blank: false
    }
}
