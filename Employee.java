import java.io.BufferedWriter;
import java.io.IOException;

public class Employee{

    private final BufferedWriter myWriter;
    private final Branch branch;
    private final String name;
    private String position;
    private int promotionPoints;

    /**
     * constructor to initialize employee object and its data fields
     * @param name name of the employee
     * @param position job of the employee
     * @param branch where employee works
     * @param myWriter writer object to write to the file
     */

    Employee(String name, String position, Branch branch, BufferedWriter myWriter){
        this.myWriter = myWriter;
        this.branch = branch;
        this.name = name;
        this.position = position;
        this.promotionPoints = 0;
    }

    /**
     * updates bonuses and promotion points of the employee
     * @param score monthly score that employee has taken
     * @throws IOException when cannot reach an insider method
     */

    public void performanceUpdate(int score) throws IOException {
        int promotionPoint = score / 200;
        if(score >= 0) {
            this.promotionPoints += promotionPoint;
            if(this.promotionPoints >= 10 && this.getPosition().equals("COOK")){
                if(!this.branch.getManagerToBe().contains(this))
                    this.branch.addManagerToBe(this);
                if(this.branch.getBranchManager().getPromotionPoints() <= -5){
                    this.branch.changeManager2();
                }
            }
            else if(this.promotionPoints >=3 && this.getPosition().equals("CASHIER")){
                if (this.branch.getNumberOfCashiers() > 1){
                    this.setPromotionPoints(this.promotionPoints-3);
                    this.setPosition("COOK");
                    myWriter.write(this.getName() + " is promoted from Cashier to Cook.\n");
                    this.branch.increaseCook();
                    this.branch.decreaseCashier();
                    this.performanceUpdate(0);
                    if(this.branch.getBranchManager().getPromotionPoints() <= -5){
                        this.branch.changeManager2();
                    }
                }
                else this.branch.setCashierToBePromoted(this);
            }
            else if(this.promotionPoints > -5){
                if(this.branch.getCashierToDismiss() == this){
                    this.branch.setCashierToDismiss(null);
                }
                else if(this.branch.getCookToDismiss() == this){
                    this.branch.setCookToDismiss(null);
                }
                else if(this.branch.getCourierToDismiss() == this){
                    this.branch.setCourierToDismiss(null);
                }
            }
        }
        else {
            this.promotionPoints += promotionPoint;
            if(this.promotionPoints < 10 && this.branch.getManagerToBe().contains(this)){
                this.branch.getManagerToBe().remove(this);
            }
             else if(this.promotionPoints < 3 && this.branch.getCashierToBePromoted() == this){
                this.branch.setCashierToBePromoted(null);
            }
            else if(this.promotionPoints <= -5){
                this.branch.dismissal(this);
            }
        }
    }


    /**
     * gives bonus when employee is not able to leave
     */
    public void giveBonus(){
        this.branch.giveBonus();
    }

    /**
     * @return name of the employee
     */

    public String getName() {
        return name;
    }

    /**
     * @return position of the employee
     */

    public String getPosition() {
        return position;
    }

    /**
     * sets a new position for the employee
     * @param position the new position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return promotion points of the employee
     */
    public int getPromotionPoints() {
        return promotionPoints;
    }


    /**
     * changes the promotion points for the employee
     * @param promotionPoints new value of promotion points
     */
    public void setPromotionPoints(int promotionPoints) {
        this.promotionPoints = promotionPoints;
    }
}