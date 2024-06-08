public class City {
    private final String name;
    private MyHashMap<String, Branch> cityMap;

    /**
     * city object constructor
     * @param name the name of the city
     */
    public City(String name){
        this.name = name;
        this.cityMap = new MyHashMap<String, Branch>();
    }

    /**
     * adds branch to the city
     * @param branch the branch to add
     */
    public void addBranch(Branch branch){
        cityMap.put(branch.getName(), branch);
    }

    /**
     * @return hashmap that is located to the city
     */
    public MyHashMap<String, Branch> getCityMap() {
        return cityMap;
    }
}
