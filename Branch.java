import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Branch{


    private final BufferedWriter myWriter;
    private final String name;
    private MyHashMap<String, Employee> branchMap;
    private Employee branchManager;
    private ArrayList<Employee> managerToBe;
    private Employee cashierToBePromoted;
    private Employee cookToDismiss;
    private Employee cashierToDismiss;
    private Employee courierToDismiss;

    private int numberOfCooks, numberOfCouriers, numberOfCashiers;

    private int monthlyBonus, overallBonus;

    /**
     * this is a branch constructor
     * @param name the name of the branch
     * @param myWriter the object that writes to the file
     */
    public Branch(String name, BufferedWriter myWriter){
        this.myWriter = myWriter;
        this.name = name;
        this.branchMap = new MyHashMap<>();
        this.managerToBe = new ArrayList<>();
        this.branchManager = null;
        this.cashierToBePromoted = null;
    }

    /**
     * adds employee to the branch
     * @param employee object to add to the branch
     * @throws IOException if employee has some missing data
     */
    public void addEmployee(Employee employee) throws IOException {
        String name = employee.getName();
        this.branchMap.put(name, employee);
        String position = employee.getPosition();
        if(position.equals("MANAGER") && this.branchManager == null){
            this.branchManager = employee;
        }
        else if (position.equals("COOK")){
            numberOfCooks ++;
            if(this.cookToDismiss != null){
                this.dismissal(this.cookToDismiss);
                this.cookToDismiss = null;
            }
            else{
                try {
                    if(this.branchManager.getPromotionPoints() <= -5){
                        this.changeManager2();
                    }
                }catch (Exception e){}
            }
        }
        else if (position.equals("CASHIER")){
            numberOfCashiers ++;
            if(this.cashierToBePromoted != null){
                Employee employeeToPromote = this.cashierToBePromoted;
                employeeToPromote.setPromotionPoints(employeeToPromote.getPromotionPoints()-3);
                employeeToPromote.setPosition("COOK");
                myWriter.write(employeeToPromote.getName() + " is promoted from Cashier to Cook.\n");
                this.numberOfCooks ++;
                this.numberOfCashiers --;
                this.cashierToBePromoted = null;
                employeeToPromote.performanceUpdate(0);
                if(this.branchManager.getPromotionPoints() <= -5){
                    this.changeManager2();
                }
            }
            else if(this.cashierToDismiss != null){
                this.dismissal(cashierToDismiss);
                this.cashierToDismiss = null;
            }
        }
        else if (position.equals("COURIER")) {
            numberOfCouriers ++;
            if(this.courierToDismiss != null){
                this.dismissal(courierToDismiss);
                this.courierToDismiss = null;
            }
        }
    }


    /**
     * removes the employee from the branch
     * @param employee object to remove from the branch
     */
    public void remove(Employee employee){
        String position = employee.getPosition();
        if(position.equals("COOK")){
            if(this.managerToBe.contains(employee)){
                this.managerToBe.remove(employee);
            }
            else if(this.cookToDismiss == employee){
                this.cookToDismiss = null;
            }
            this.numberOfCooks --;
        }
        else if(position.equals("CASHIER")){
            if(this.cashierToDismiss == employee){
                this.cashierToDismiss = null;
            }
            else if(this.cashierToBePromoted == employee){
                this.cashierToBePromoted = null;
            }
            this.numberOfCashiers--;
        }
        else if(position.equals("COURIER")) this.numberOfCouriers--;
        this.branchMap.remove(employee.getName());
    }

    /**
     * checks if employee can leave, if yes, makes employee leave the branch
     * @param employee object that wants to leave the branch
     * @throws IOException when employee has some missing data
     */
    public void leave(Employee employee) throws IOException {
        int promotionPoints = employee.getPromotionPoints();
        String position = employee.getPosition();
        if(position.equals("MANAGER")){
            this.changeManager1();
        }
        else if(position.equals("COOK") && numberOfCooks == 1){
            if (promotionPoints > -5) employee.giveBonus();
        }
        else if(position.equals("CASHIER") && numberOfCashiers == 1){
            if (promotionPoints > -5) employee.giveBonus();
        }
        else if(position.equals("COURIER") && numberOfCouriers == 1){
            if (promotionPoints > -5) employee.giveBonus();
        }
        else {
            remove(employee);
            myWriter.write(employee.getName() + " is leaving from branch: " + this.name + ".\n");
        }
    }

    /**
     * checks if the employee can be dismissed, if yes, then dismisses the employee
     * @param employee object to be dismissed from the branch
     * @throws IOException when employee has some missing data
     */

    public void dismissal(Employee employee) throws IOException {
        String position = employee.getPosition();
        if(position.equals("MANAGER")){
            this.changeManager2();
        }
        else if(position.equals("COOK") && numberOfCooks == 1){
            this.cookToDismiss = employee;
        }
        else if(position.equals("CASHIER") && numberOfCashiers == 1){
            this.cashierToDismiss = employee;
        }
        else if(position.equals("COURIER") && numberOfCouriers == 1){
            this.courierToDismiss = employee;
        }
        else {
            remove(employee);
            myWriter.write(employee.getName() + " is dismissed from branch: " + this.name + ".\n");
        }
    }

    /**
     * when manager wants to leave this method is called
     * checks if manager can leave from the branch, changes the manager if possible
     * @throws IOException when there is no writer object
     */
    public void changeManager1() throws IOException {
        if(!this.managerToBe.isEmpty() && this.numberOfCooks > 1){
            Employee nextManager = getNextManager();
            int promotionPoints = nextManager.getPromotionPoints();
            nextManager.setPromotionPoints(promotionPoints - 10);
            nextManager.setPosition("MANAGER");
            numberOfCooks--;
            myWriter.write(this.branchManager.getName() + " is leaving from branch: " + this.name + ".\n");
            this.branchMap.remove(branchManager.getName());
            this.getManagerToBe().remove(nextManager);
            this.branchManager = nextManager;
            myWriter.write(this.branchManager.getName() + " is promoted from Cook to Manager.\n");
            }
        else if (this.branchManager.getPromotionPoints() > -5 )this.giveBonus();
    }

    /**
     * when manager is to be dismissed, this method is called
     * checks if manager can be dismissed, a new manager comes to the office if possible
     * @throws IOException when there is no writer object
     */
    public void changeManager2() throws IOException {
        if(!this.managerToBe.isEmpty() && this.numberOfCooks > 1){
            Employee nextManager = getNextManager();
            int promotionPoints = nextManager.getPromotionPoints();
            nextManager.setPromotionPoints(promotionPoints-10);
            nextManager.setPosition("MANAGER");
            numberOfCooks --;
            myWriter.write(this.branchManager.getName() + " is dismissed from branch: " + this.name + ".\n");
            this.branchMap.remove(branchManager.getName());
            this.getManagerToBe().remove(nextManager);
            this.branchManager = nextManager;
            myWriter.write(this.branchManager.getName() + " is promoted from Cook to Manager.\n");
        }
    }

    /**
     * clears the monthly bonus for the branch when a new month comes
     */
    public void clearPerformanceUpdate(){
        this.monthlyBonus = 0;
    }

    /**
     * updates the monthly and overall bonuses for the branch when an employee gets bonuses
     * @param score the monthly performance
     */
    public void performanceUpdate(int score){
        if(score > 0) {
            int bonus = score % 200;
            this.monthlyBonus += bonus;
            this.overallBonus += bonus;
        }
    }

    /**
     * gives bonus if an employee wants to leave but cannot
     */

    public void giveBonus(){
        this.monthlyBonus += 200;
        this.overallBonus += 200;
    }

    /**
     * adds to the array list when a cook has 10 or more points
     * @param employee the cook that reaches 10 or more points
     */

    public void addManagerToBe(Employee employee){
        managerToBe.add(employee);
    }

    /**
     * list of manager candidates
     * @return arraylist of manager candidates
     */
    public ArrayList<Employee> getManagerToBe() {
        return managerToBe;
    }

    /**
     * check who is the first in the list of manager candidates in order of reaching 10 points first
     * @return first element of manager candidates
     */
    public Employee getNextManager() {
        return managerToBe.remove(0);
    }

    /**
     * @return number of cashiers in the branch
     */
    public int getNumberOfCashiers() {
        return numberOfCashiers;
    }

    /**
     * @return name of the branch
     */
    public String getName() {
        return name;
    }

    /**
     * @return monthly bonus of the branch
     */
    public int getMonthlyBonus() {
        return monthlyBonus;
    }

    /**
     * @return overall bonus of the branch
     */

    public int getOverallBonus() {
        return overallBonus;
    }

    /**
     * @return manager of the branch
     */
    public Employee getBranchManager() {
        return branchManager;
    }

    /**
     * @return hashmap that is located to the branch
     */
    public MyHashMap<String, Employee> getBranchMap() {
        return branchMap;
    }

    /**
     * increase number of cooks by 1 in the branch
     */
    public void increaseCook(){
        this.numberOfCooks++;
    }

    /**
     * decrease number of cashiers by 1 in the branch
     */
    public void decreaseCashier(){
        this.numberOfCashiers--;
    }

    /**
     * sets the cashier to promote
     * @param cashierToBePromoted the cashier to be promoted
     */

    public void setCashierToBePromoted(Employee cashierToBePromoted) {
        this.cashierToBePromoted = cashierToBePromoted;
    }

    /**
     * @return cashier who will promote when it is possible
     */

    public Employee getCashierToBePromoted() {
        return cashierToBePromoted;
    }

    /**
     * @return cashier who will dismiss from the branch when it is possible
     */
    public Employee getCashierToDismiss() {
        return cashierToDismiss;
    }

    /**
     * @return cook who will dismiss from the branch when it is possible
     */
    public Employee getCookToDismiss() {
        return cookToDismiss;
    }

    /**
     * @return courier who will dismiss from the branch when it is possible
     */
    public Employee getCourierToDismiss() {
        return courierToDismiss;
    }

    /**
     * sets a cook to dismiss when it is not possible to dismiss
     * @param cookToDismiss the cook to be dismissed
     */
    public void setCookToDismiss(Employee cookToDismiss) {
        this.cookToDismiss = cookToDismiss;
    }

    /**
     * sets a cashier to dismiss when it is not possible to dismiss
     * @param cashierToDismiss the cashier to be dismissed
     */
    public void setCashierToDismiss(Employee cashierToDismiss) {
        this.cashierToDismiss = cashierToDismiss;
    }

    /**
     * sets a courier to dismiss when it is not possible to dismiss
     * @param courierToDismiss the courier to be dismissed
     */
    public void setCourierToDismiss(Employee courierToDismiss) {
        this.courierToDismiss = courierToDismiss;
    }
}
