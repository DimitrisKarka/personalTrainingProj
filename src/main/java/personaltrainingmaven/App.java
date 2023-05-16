package personaltrainingmaven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        String url = "jdbc:mysql://localhost:3306/PersonalTraining";
        String username = "root";
        String password = "";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");//loading the MySQL JDBC driver class at runtime needed to create the conection
            Connection conection = DriverManager.getConnection(url, username, password);//creating conection
            Statement statement = conection.createStatement();//creating statement
            add(statement);
            conection.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private static void add (Statement statement){
        System.out.println();
        System.out.println("Please add new trainee's info(id, full_name, phone_number, sex, age, height, weight, lvl).\n" + 
        "-Id and full_name cannot be null.\n" +
        "-Full name should be seperated with an underscore (eg Jim_Halpert).\n" +
        "-If you don't know the rest of the fields just don't add them.\n" + 
        "-Height should be in m (eg \"1.63m\") and weight in kg (eg \"70.6kg\")\n" +
        "*Remember that lvl sex can only take one of these Values \"male\", \"female\", \"non_binary\"\n" +
        "*and lvl can only take one of these values \"novice\", \"intermediate\", \"athletic\", \"athlete\"\n\n" +
        "The input must be a string of each field you currently know (mandatory id and full_name) followed by a comma (\",\")\n" +
        "for example \"2, george, 2341442313\"");
        System.out.println();
        System.out.println("Input:");
        String input;
        String id = "0", full_name = "no_name", phone_number = "NA", sex = "", age = "0", height = "0m", weight = "0kg", lvl = "NA";
        Scanner scan = new Scanner(System.in);
        input = scan.nextLine();
        scan.close();
        input = input.replaceAll(" ", "");
        String[] fields = input.split(",");
        Queue<String> fieldQueue = new LinkedList<String>();
        int i = 0;       
        for (String field : fields) {
            if( i == 7) break;
            else{
                fieldQueue.add(field);
            }
            i++;
        }
        for (String item : fieldQueue) {
            System.out.println(item);
        }
        i = 0;
        System.out.println();
        while(!fieldQueue.isEmpty()){
            if(i == 0){//first entry is id mandatory
                id = fieldQueue.remove();
            }
            else if(i == 1){//secodn entry is full_name mandatory
                full_name = fieldQueue.remove();
            }
            else{
                String curValue = fieldQueue.peek();
                if(curValue.length() == 1){//a single char string if it isnt a number which could be age it is a wrong value, doesnt beong to any field
                    int asciiValue = curValue.charAt(0);
                    if (asciiValue < 48 || asciiValue > 57) {
                        fieldQueue.remove();
                        System.out.println("the value \"" + curValue + "\"cannot be asigned to any field\n");
                    }
                }
                boolean checkIfPhone = true;
                if(curValue.length() > 2){
                    for (int j = 1; j < curValue.length(); j++) {
                        int asciiValue = curValue.charAt(j);
                        if (asciiValue < 48 || asciiValue > 57) {
                            checkIfPhone  = false; // Character is not a numeric digit
                        }
                    }
                }
                else{
                    checkIfPhone = false;
                }
                boolean checkIfage = true;
                if(curValue.length() > 2){
                    checkIfage = false;
                }
                else if(curValue.length() == 2){
                    for (int j = 0; j < 2; j++) {
                        int asciiValue = curValue.charAt(j);
                        if (asciiValue < 48 || asciiValue > 57) {
                            checkIfage  = false; // Character is not a numeric digit
                        }
                    }
                }
                if(checkIfPhone == true){
                    phone_number = fieldQueue.remove();
                }
                else if(curValue.equals("male")  || curValue.equals("female") || curValue.equals("non_binary") ){
                    sex = fieldQueue.remove();
                }
                else if(curValue.contains("m") && curValue.charAt(1) == '.'){
                    height = fieldQueue.remove();
                }
                else if(curValue.contains("kg")){
                    weight = fieldQueue.remove();
                }
                else if(checkIfage == true){//the only field with 2 or less chars (apart from id or name wich are compulsory)
                    age = fieldQueue.remove();
                }
                else if(curValue.equals("novice") || curValue.equals("intermediate")  || curValue.equals("athletic" )|| curValue.equals("athlete")){
                    lvl = fieldQueue.remove();
                }
                else{
                    System.out.println("the value \"" + curValue + "\"cannot be asigned to any field\n");
                    fieldQueue.remove();
                }
            }
            i++;
        }
        System.out.println("id = " + id );
        System.out.println("full_name = " + full_name );
        System.out.println("phone_number = " + phone_number );
        System.out.println("sex = " + sex);
        System.out.println("age = " + age );
        System.out.println("height = " + height);
        System.out.println("weight = " + weight );
        System.out.println("lvl = " + lvl);
        
        try{
            statement.executeUpdate(
                "INSERT INTO trainee (id, full_name, phone_number, sex, age, height, weight,lvl)" +
                "VALUES (" + Integer.valueOf(id) + ", '" + full_name +"', '" + phone_number + "', '" + sex + "', " + Integer.valueOf(age) + ", '" + height + "', '" + weight + "', '" + lvl + "')");
            }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
