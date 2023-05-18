package personaltrainingmaven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
        String password = "root";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");//loading the MySQL JDBC driver class at runtime needed to create the conection
            Connection conection = DriverManager.getConnection(url, username, password);//creating conection
            Statement statement = conection.createStatement();//creating statement
            int choice = 0;
            System.out.println("\nWelcome to PersonalTrainingManager2000TURBO\n"+
            "please choose one of the below functions\n"+
            "1.Add profile\n2.Alter profile\n3.Remove profile\n4.Export all profiles in pdf(or excel we ll see)\n5.Export curent schedule.xls\n" +
            "\nType your choice:");
            Scanner scan = new Scanner(System.in);
            choice = scan.nextInt();
            while(choice < 1 || choice > 5){
                System.out.println("Choice number " + choice +" doesn't exist, please type again:" );
                choice = scan.nextInt();
            }
            switch (choice){
                case 1:
                    add(statement, scan);
                break;
                case 2:
                    alter(statement, scan);
                break;
                case 3:
                    remove(statement, scan);
                break;
                case 4:
                System.out.println("export all profiles");
                break;
                case 5:
                System.out.println("export current schedule");
                break;
            }
            scan.close();
            conection.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    private static void add (Statement statement, Scanner scan){
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
        scan.nextLine();//for some reason needed here apparently to clear some leftover garbege value. Dont know from where..
        input = scan.nextLine();
        input = input.replaceAll(" ", "");
        String[] fields = input.split(",");
        Queue<String> fieldQueue = new LinkedList<String>();
        int i = 0;       
        for (String field : fields) {
            if( i == 8) break;
            else{
                fieldQueue.add(field);
            }
            i++;
        }
        for (String item : fieldQueue) {
            System.out.println(item);
        }
        i = 0;
        while(!fieldQueue.isEmpty()){
            if(i == 0){//first entry is id, mandatory
                id = fieldQueue.remove();
            }
            else if(i == 1){//second entry is full_name, mandatory
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
        /*
        System.out.println("id = " + id );
        System.out.println("full_name = " + full_name );
        System.out.println("phone_number = " + phone_number );
        System.out.println("sex = " + sex);
        System.out.println("age = " + age );
        System.out.println("height = " + height);
        System.out.println("weight = " + weight );
        System.out.println("lvl = " + lvl);
        */
        try{
            statement.executeUpdate(
                "INSERT INTO trainee (id, full_name, phone_number, sex, age, height, weight,lvl)" +
                "VALUES (" + Integer.valueOf(id) + ", '" + full_name +"', '" + phone_number + "', '" + sex + "', " + Integer.valueOf(age) + ", '" + height + "', '" + weight + "', '" + lvl + "')");
            }
        catch(Exception e){
            System.out.println(e);
        }
        //should also put the code for the schedule update
    }

    public static void alter(Statement statement, Scanner scan){
        System.out.println("Please type the id of the trainee you want to alter (id cannot be altered):");
        int id = idChecker(statement, scan);
        System.out.println("Please type the fields you want to alter (fields: id, full_name, phone_number, sex, age, height, weight,lvl)\n" +
        "Input should be the designated fields for alteration separeted by \",\" (eg  phone_number,  sex, age)\n" +
        "*(typing zero or more than one spaces won't affect the input):");
        scan.nextLine();//for some reason needed here apparently to clear some leftover garbege value. Dont know from where..
        Queue<String> fieldQueue = new LinkedList<String>(); 
        boolean correctFieldsOrTerminator = false;
        int i = 0;   
        String input = scan.nextLine();
        input = input.replaceAll(" ", "");
        String[] fields = input.split(",");
        while(!correctFieldsOrTerminator){ 
            for (String field : fields) {
                if(fields.length > 7){
                    System.out.println("Maximum number of fields = 7. You typed " + fields.length +".\n" +
                    "Do you wish to retype the fields y or n?");
                    String yOrn = scan.nextLine();
                    while(!yOrn.equals("y") && !yOrn.equals("n")){
                        System.out.println("Please type y or n");
                        yOrn = scan.nextLine();
                    }
                    if(yOrn.equals("n")){
                        correctFieldsOrTerminator = true;
                    }
                    else {
                        System.out.println("Please retype the fields:");
                        input = scan.nextLine();
                        input = input.replaceAll(" ", "");
                        fields = input.split(",");
                        i = 0;
                    }
                    break;
                }
                if(!field.equals("full_name") && !field.equals("phone_number") && !field.equals("sex") && !field.equals("age") && !field.equals("height") && !field.equals("weight") && !field.equals("lvl")){
                    fieldQueue.clear();
                    System.out.println("The field \"" + field + "\" does not exist.\nDo you wish to retype the fields y or n?");
                    String yOrn = scan.nextLine();
                    while(!yOrn.equals("y") && !yOrn.equals("n")){
                        System.out.println("Please type y or n");
                        yOrn = scan.nextLine();
                    }
                    if(yOrn.equals("n")){
                        correctFieldsOrTerminator = true;
                    }
                    else {
                        System.out.println("Please retype the fields:");
                        input = scan.nextLine();
                        input = input.replaceAll(" ", "");
                        fields = input.split(",");
                        i = 0;
                    }
                    break;
                }
                else{
                    //fieldQueue can take multple times the same filed(eg 3 times "age") but i dont think that logical
                    //mistake is worth handling
                    fieldQueue.add(field);
                    i++;
                    if(i == fields.length){
                        correctFieldsOrTerminator = true;
                    }
                }
            }
        }
        while(!fieldQueue.isEmpty()){
            System.out.println("Please type the new " + fieldQueue.remove() + ":");
        }
    }

    public static void remove(Statement statement, Scanner scan){
        System.out.println("Please type the id of the trainee you want to remove:");
        int id = idChecker(statement, scan);
        try{
            statement.executeUpdate("DELETE FROM trainee WHERE id = " + id);
        }
        catch(Exception e){
            System.out.println(e);
        }    
    }

    private static int idChecker(Statement statement, Scanner scan){
        int id = scan.nextInt();
        try{
            ResultSet resultSet = statement.executeQuery("SELECT id FROM trainee"); 
            LinkedList<Integer> idList = new LinkedList<Integer>();
            while (resultSet.next()) {
                idList.add(resultSet.getInt("id"));
            }
            while(!idList.contains(id)){
                System.out.println("The id " + id + " doesn't exist please type again:");
                id = scan.nextInt();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return id;
    }

}
