import java.io.*;
import java.util.Scanner;


public class project2 {
    public static void main(String[] args) throws IOException {

        // Initialize a new hash map with keys of strings and values of city objects
        MyHashMap<String, City> cities = new MyHashMap<>();
        String initialFile;
        String inputFile;
        String outputFile;
        try{
            initialFile = args[0];
            inputFile = args[1];
            outputFile = args[2];
        } catch (Exception e) {
            initialFile = "large_initial5.txt";
            inputFile = "large5.txt";
            outputFile = "large_output5.txt";
        }
        File file1=new File(initialFile);
        Scanner reader = new Scanner(file1);
        FileWriter writer = new FileWriter(outputFile);
        BufferedWriter myWriter = new BufferedWriter(writer);
        while (reader.hasNext()){
            String line = reader.nextLine();
            String[] data = line.split(",");
            String city = data[0].strip();
            String branch = data[1].strip();
            String name = data[2].strip();
            String position = data[3].strip();

            // checks if cities hash map contains the city, puts a new city object to the hash map if not
            if(!cities.containsKey(city)){
                cities.put(city, new City(city));
            }
            City cityObject = cities.get(city);

            //checks if city has the branch, if not adds a new branch to the city
            if(!cityObject.getCityMap().containsKey(branch)){
                cityObject.addBranch(new Branch(branch, myWriter));
            }
            Branch branchMap = cityObject.getCityMap().get(branch);

            // adds employee to the branch
            branchMap.addEmployee(new Employee(name, position, branchMap, myWriter));
        }
        reader.close();
        File file2=new File(inputFile);
        Scanner reader2 = new Scanner(file2);
        while (reader2.hasNext()) {
            try {
                String nextS = reader2.next();
                if (nextS.equals("LEAVE:")) {
                    String line = reader2.nextLine();
                    String[] data = line.split(",");
                    String city = data[0].strip();
                    String branch = data[1].strip();
                    String name = data[2].strip();
                    City cityObject = cities.get(city);
                    Branch branchObject = cityObject.getCityMap().get(branch);
                    Employee employee = branchObject.getBranchMap().get(name);

                    // calls the method that if employee can leave, makes leaving process happen if yes
                    branchObject.leave(employee);
                } else if (nextS.equals("PERFORMANCE_UPDATE:")) {
                    String line = reader2.nextLine();
                    String[] data = line.split(",");
                    String city = data[0].strip();
                    String branch = data[1].strip();
                    String name = data[2].strip();
                    int score = Integer.parseInt(data[3].strip());
                    City cityObject = cities.get(city);
                    Branch branchObject = cityObject.getCityMap().get(branch);
                    Employee employee = branchObject.getBranchMap().get(name);

                    // updates the bonuses and promotion points for the employee
                    employee.performanceUpdate(score);

                    // updates the bonuses for the branch
                    branchObject.performanceUpdate(score);
                } else if (nextS.equals("ADD:")) {
                    String line = reader2.nextLine();
                    String[] data = line.split(",");
                    String city = data[0].strip();
                    String branch = data[1].strip();
                    String name = data[2].strip();
                    String position = data[3].strip();
                    City cityObject = cities.get(city);
                    Branch branchObject = cityObject.getCityMap().get(branch);

                    // if branch contains the employee, s/he cannot be added to the branch
                    if(branchObject.getBranchMap().containsKey(name)){
                        myWriter.write("Existing employee cannot be added again.\n");
                    }

                    // else adds the employee to the branch
                    else branchObject.addEmployee(new Employee(name, position, branchObject, myWriter));
                } else if (nextS.equals("PRINT_MONTHLY_BONUSES:")) {
                    String line = reader2.nextLine();
                    String[] data = line.split(",");
                    String city = data[0].strip();
                    String branch = data[1].strip();
                    City cityObject = cities.get(city);
                    Branch branchObject = cityObject.getCityMap().get(branch);

                    // writes the monthly bonus for the branch
                    myWriter.write("Total bonuses for the " + branch + " branch this month are: " + branchObject.getMonthlyBonus() + "\n");
                } else if (nextS.equals("PRINT_MANAGER:")) {
                    String line = reader2.nextLine();
                    String[] data = line.split(",");
                    String city = data[0].strip();
                    String branch = data[1].strip();
                    City cityObject = cities.get(city);
                    Branch branchObject = cityObject.getCityMap().get(branch);

                    // writes the manager of the branch
                    myWriter.write("Manager of the " + branch + " branch is "  + branchObject.getBranchManager().getName() + ".\n");
                } else if (nextS.equals("PRINT_OVERALL_BONUSES:")) {
                    String line = reader2.nextLine();
                    String[] data = line.split(",");
                    String city = data[0].strip();
                    String branch = data[1].strip();
                    City cityObject = cities.get(city);
                    Branch branchObject = cityObject.getCityMap().get(branch);

                    // writes the overall bonus for the branch
                    myWriter.write("Total bonuses for the " + branch + " branch are: " + branchObject.getOverallBonus()+"\n");
                } else {

                    // clears all the monthly bonuses for all the branches when a new month comes
                    for (Entry<String, City> set : cities.entrySet()) {
                        for (Entry<String, Branch> set2 :
                                set.getValue().getCityMap().entrySet()) {
                            set2.getValue().clearPerformanceUpdate();
                        }
                    }
                }
            }
            // when there is not such employee
            catch (NullPointerException e){
                myWriter.write("There is no such employee.\n");
            }
        }
        reader2.close();
        myWriter.close();
    }
}

