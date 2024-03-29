package personaltrainingmaven;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mysql.cj.jdbc.result.ResultSetMetaData;


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
            "1.Add trainee's profile\n2.Add trainee's schedule\n3.Alter trainee profile\n4.Alter trainee schedule\n5.Remove profile\n6.Export curent trainees.xls\n7.Export current schedule" +
            "\nType your choice:");
            Scanner scan = new Scanner(System.in);
            choice = scan.nextInt();
            while(choice < 1 || choice > 7){
                System.out.println("Choice number " + choice +" doesn't exist, please type again:" );
                choice = scan.nextInt();
            }
            switch (choice){
                case 1:
                    addTrainee(statement, scan);//done
                break;
                case 2:
                    addAlterSchedule(statement, scan);//done
                break;
                case 3:
                    alterTrainee(statement, scan);//done
                break;
                case 4:
                    addAlterSchedule(statement, scan);//done
                break;
                case 5:
                    remove(statement, scan);//done
                break;
                case 6:
                    exportTrainees(statement, scan);//done
                break;
                case 7:
                    exportSchedule(statement, scan);
                break;
            }
            scan.close();
            conection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTrainee (Statement statement, Scanner scan){
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
        while(fields.length < 2){
            System.out.println("It is mandatory to type at least two fields (\"id\", \"full_name\")");
            input = scan.nextLine();
            input = input.replaceAll(" ", "");
            fields = input.split(",");
        }
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
        try{
            statement.executeUpdate(
                "INSERT INTO trainee (id, full_name, phone_number, sex, age, height, weight,lvl)" +
                "VALUES (" + Integer.valueOf(id) + ", '" + full_name +"', '" + phone_number + "', '" + sex + "', " + Integer.valueOf(age) + ", '" + height + "', '" + weight + "', '" + lvl + "')" 
            );
            System.out.println("Addition completed.");
        }
        catch(Exception e){
            System.out.println("Addition not completed due to the below error:");
            System.out.println(e);
        }
    }

    private static void alterTrainee(Statement statement, Scanner scan){
        System.out.println("Please type the id of the trainee you want to alter (id cannot be altered):");
        int id = idChecker(statement, scan);
        System.out.println("Please type the fields you want to alter (fields: full_name, phone_number, sex, age, height, weight,lvl)\n" +
        "Input should be the designated fields for alteration separeted by \",\" (eg  phone_number,  sex, age)\n" +
        "*(typing zero or more than one spaces won't affect the input):");
        scan.nextLine();//for some reason needed here apparently to clear some leftover garbage value. Dont know from where..
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
                    //fieldQueue can take multple times the same filed(eg 3 times "age") but i dont think that logical mistake is worth handling right now
                    fieldQueue.add(field);
                    i++;
                    if(i == fields.length){
                        correctFieldsOrTerminator = true;
                    }
                }
            }
        }
        String alteredInput;
        while(!fieldQueue.isEmpty()){
            System.out.println("Please type the new " + fieldQueue.peek() + ":");
            switch (fieldQueue.peek()){
                case "full_name":
                    alteredInput = scan.nextLine();
                    alterField(statement, fieldQueue, alteredInput, id);
                break;
                case "phone_number":
                    alteredInput = scan.nextLine();
                    boolean checkIfPhone = false;
                    while(!checkIfPhone){
                        checkIfPhone = true;
                        if(alteredInput.length() > 2){
                            for (int j = 1; j < alteredInput.length(); j++) {
                                int asciiValue = alteredInput.charAt(j);
                                if (asciiValue < 48 || asciiValue > 57) {
                                    checkIfPhone  = false; // Character is not a numeric digit
                                }
                            }
                        }
                        if(checkIfPhone == false){
                            System.out.println("\"" + alteredInput + "\" is not a valid value for \"phone_number\". Please type again:");
                            alteredInput = scan.nextLine();
                        }
                        else checkIfPhone = true;
                    }
                    alterField(statement, fieldQueue, alteredInput, id);
                break;
                case "sex":
                    alteredInput = scan.nextLine();
                    while(!alteredInput.equals("male")  && !alteredInput.equals("female") && !alteredInput.equals("non binary")){
                        System.out.println("\"" + alteredInput + "\" is not a valid input for field \"sex\". Please type \"male\", \"female\" or \"non binary\":");
                        alteredInput = scan.nextLine();
                    }
                    alterField(statement, fieldQueue, alteredInput, id);        
                break;
                case "age":
                    alteredInput = scan.nextLine();
                    boolean checkIfAge = false;
                    while(!checkIfAge){
                        checkIfAge = true;
                        if(alteredInput.length() > 2){
                            System.out.println("\"" + alteredInput + "\" is not a valid input for field \"age\". Please type again:");
                            alteredInput = scan.nextLine();
                            checkIfAge = false;
                        }
                        else{
                            for (int j = 0; j < alteredInput.length(); j++) {
                                int asciiValue = alteredInput.charAt(j);
                                if (asciiValue < 48 || asciiValue > 57) {
                                    checkIfAge  = false; // Character is not a numeric digit
                                }
                            }
                            if(checkIfAge == false){
                                System.out.println("\"" + alteredInput + "\" is not a valid input for field \"age\". Please type again:");
                                alteredInput = scan.nextLine();
                            }
                            else checkIfAge = true;
                        }
                    }
                    alterField(statement, fieldQueue, alteredInput, id);           
                break;
                case "height":
                    alteredInput = scan.nextLine();
                    while(!(alteredInput.contains("m") && alteredInput.charAt(1) == '.')){
                        System.out.println("\"" + alteredInput + "\" is not a valid input for field \"height\". Please type again (eg 1.56m):");
                        alteredInput = scan.nextLine();
                    }
                    alterField(statement, fieldQueue, alteredInput, id);
                break;
                case "weight":
                    alteredInput = scan.nextLine();
                    while(!alteredInput.contains("kg")){
                        System.out.println("\"" + alteredInput + "\" is not a valid input for field \"weight\". Please type again (80.5kg or 80kg no decimals needed):");
                        alteredInput = scan.nextLine();
                    }
                    alterField(statement, fieldQueue, alteredInput, id);
                break;
                case "lvl":
                    alteredInput = scan.nextLine();
                    while(!(alteredInput.equals("novice") || alteredInput.equals("intermediate")  || alteredInput.equals("athletic" )|| alteredInput.equals("athlete"))){
                        System.out.println("\"" + alteredInput + "\" is not a valid input for field \"lvl\". Please type again \"novice\", \"intermediate\", \"athletic\" or \"athlete\":");
                        alteredInput = scan.nextLine();  
                    }
                    alterField(statement, fieldQueue, alteredInput, id);
                break;
            }
            fieldQueue.remove();
        }
    }

    private static void addAlterSchedule(Statement statement, Scanner scan){
        System.out.println("Please type the id of the trainee whos schedule you want to add/alter:");
        int id = idChecker(statement, scan);
        LinkedList<Integer> currId = new LinkedList<Integer>();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT id FROM weekly_schedule");
                while (resultSet.next()) {
                    currId.add(resultSet.getInt("id"));         
                }
            }
            catch (Exception e){
                System.out.println("something went motherfucking wrong i guess");
                System.out.println(e);
            }
        if(currId.contains(id)){
            try{
                statement.executeUpdate("UPDATE weekly_schedule SET monday = \" \", tuesday = \" \", wednesday = \" \", thursday = \" \", friday = \" \", saturday = \" \", sunday = \" \" where id = " + id);
                System.out.println("\nCurrent schedule of trainer with id = " + id + " has been deleted:\n");
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        System.out.println("Please add the weekly schedule of the trainee, or press space if there is not one at the time.\n" +
        "The input format should be the day followed by the hours in 24:00 format (starting-ending)\n"+
        "and each day added should be separated with a comma (,) from the previus:\n"+
        "e.g. Monday 12:00-13:00, Thursday 12:00-13:00, Friday 16:15-17:30\n" +
        "*time can take values like 34:78-09:34 so do not be stupid. write up to 23 HOURS AND 59 MINUTES!(23:59 max hour)");
        scan.nextLine();//for some reason needed here apparently to clear some leftover garbage value. Dont know from where..
        boolean notCorrectDay  = true;
        boolean notCorrectHour  = true;
        String day = "not a day";
        String hour = "not an hour";
        Queue<String> allDays = new  LinkedList<String>();
        Queue<String> allHours = new  LinkedList<String>();
        while(notCorrectDay == true || notCorrectHour == true){
            String schedule = scan.nextLine();
            if(!schedule.equals(" ")){
                schedule = schedule.replaceAll(" ", "");
                String[] daysAndHours = schedule.split(",");
                Pattern pattern = Pattern.compile("[a-zA-Z]+");
                Matcher matcherDay = pattern.matcher(schedule);
                while (matcherDay.find()) {
                    day = matcherDay.group().toLowerCase();
                    if(!(day.equals("monday") || day.equals("tuesday") || day.equals("wednesday") || day.equals("thursday") || day.equals("friday") || day.equals("saturday") || day.equals("sunday"))){
                        System.out.println("The string " + day + " is not a day you moron\n" +
                        "Please type the schedule again correctly or just press space");  
                        allDays.clear();                
                        notCorrectDay  = true;
                    }
                    else {
                        allDays.add(day);
                        notCorrectDay = false;
                    }
                }
                if(notCorrectDay == true){
                    allHours.clear();
                }
                else{
                    for(String curval : daysAndHours){
                        char[] arrSchedule = curval.toCharArray();
                        hour = "";
                        for(char character : arrSchedule){
                            int asciiValue = character;
                            if((asciiValue > 47 && asciiValue < 58) || character == ':' || character == '-'){
                                hour = hour + character;
                            }
                        }
                        if(hour.contains(":") || hour.contains("-") || (hour.length() < 12 && hour.length() > 8)){
                            String[] splitTime = hour.split("-");//if there is no character after the "-" an error occurs but i dont think its a significant ocassion for error handling right now
                            String startTime = splitTime[0];
                            String endTime = splitTime[1];
                            int colonIndexStart = startTime.indexOf(":");
                            int colonIndexEnd = startTime.indexOf(":");
                            String minutesOfStart = startTime.substring(colonIndexStart + 1);
                            String minutesOfEnd = endTime.substring(colonIndexEnd + 1);
                            if(minutesOfStart.length() < 2 || minutesOfEnd.length() < 2){
                                System.out.println("The string " + hour + " is not an hour you moron\n" +
                                "Please type the schedule again correctly or just press space");
                                allHours.clear();
                                allDays.clear();
                                notCorrectHour  = true;
                            }   
                            else{
                                allHours.add(hour);
                                notCorrectHour = false;
                                hour = "not an ok";
                            }                         
                        }
                        else{
                            System.out.println("The string " + hour + " is not an hour you moron\n" +
                            "Please type the schedule again correctly or just press space");
                            allHours.clear();
                            allDays.clear();
                            notCorrectHour  = true;
                        }
                    }
                }
                if(notCorrectDay == false && notCorrectHour == false){
                String full_name = "no_name";
                try {
                    ResultSet resultSet = statement.executeQuery("SELECT full_name FROM trainee WHERE id = " + id);
                    if (resultSet.next()) {
                        full_name = resultSet.getString("full_name");
                    }
                }
                catch (Exception e) {
                    //i dont think this exeption can happen because the id will always be correct since it is checked above
                }
                try {
                    if(!currId.contains(id)){
                    statement.executeUpdate("INSERT INTO weekly_schedule (id, full_name)  VALUES (" + id + ", '" + full_name + "')" 
                    ); 
                    }
                    int loops = allDays.size();
                    for(int i = 0; i < loops; i++){
                        statement.executeUpdate("UPDATE weekly_schedule SET " + allDays.remove() + " = \"" + allHours.remove() + "\" WHERE id = " + id);
                    }
                    System.out.println("All additions were succesful");
                } catch (Exception e) {
                    System.out.println("Error: no value was added due to:");
                    System.out.println(e);
                }
                }
            }    
            else {
                System.out.println("Schedule not added");
                notCorrectDay = false;
                notCorrectHour = false;
            }                
        }
    }

    private static void remove(Statement statement, Scanner scan){
        System.out.println("Please type the id of the trainee you want to remove:");
        int id = idChecker(statement, scan);
        try{
            statement.executeUpdate("DELETE FROM trainee WHERE id = " + id);
            statement.executeUpdate("DELETE FROM weekly_schedule WHERE id = " + id);
        }
        catch (Exception e) {
            System.out.println("Deletion not completed due to the below error:");
            System.out.println(e);
        }  
    }

    private static void exportTrainees(Statement statement, Scanner scan){
        Document document = new Document(PageSize.A4);
        try{
            PdfWriter.getInstance(document, new FileOutputStream("AllTheTrainees.pdf"));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
            Chunk chunk = new Chunk("TRAINEES", font);
            Paragraph paragraph = new Paragraph(chunk);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(20); // Set the desired spacing after the paragraph
            document.add(paragraph);
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            float[] columnWidths = {3f, 14f, 12f, 10f, 4f, 6f, 6f, 10f};
            table.setWidths(columnWidths);
            table.addCell("id");
            table.addCell("full_name");
            table.addCell("phone_number");
            table.addCell("sex");
            table.addCell("age");
            table.addCell("height");
            table.addCell("weight");
            table.addCell("lvl");
            table.completeRow();            
            try{
                ResultSet resultSet = statement.executeQuery("SELECT * FROM trainee");
                ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
                while (resultSet.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        table.addCell(resultSet.getString(i));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            document.add(table);
            document.close();
            System.out.println("Trainees export completed.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void exportSchedule(Statement statement, Scanner scan){
        Document document = new Document(PageSize.A4);
        try{
            PdfWriter.getInstance(document, new FileOutputStream("CurrentSchedule.pdf"));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
            Chunk chunk = new Chunk("WEEKLY SCHEDULE", font);
            Paragraph paragraph = new Paragraph(chunk);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(20); // Set the desired spacing after the paragraph
            document.add(paragraph);
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            float[] columnWidths = {3f, 14f, 8f, 8f, 10f, 8f, 7f, 8f, 8f};
            table.setWidths(columnWidths);
            table.addCell("id");
            table.addCell("full_name");
            table.addCell("Monday");
            table.addCell("Tuesday");
            table.addCell("Wednesday");
            table.addCell("Thursday");
            table.addCell("Friday");
            table.addCell("Saturday");
            table.addCell("Sunday");
            table.completeRow();            
            try{
                ResultSet resultSet = statement.executeQuery("SELECT * FROM weekly_schedule");
                ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
                while (resultSet.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        table.addCell(resultSet.getString(i));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            document.add(table);
            document.close();
            System.out.println("Schedule export completed.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //function idChecker is not a main function from the menu, but a helper function checking if a certain id exists in the database
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
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    //function alterFiled is not a main function from the menu, but a helper function executing the actual field alteration (used in alter profile)
    private static void alterField(Statement statement, Queue <String> fieldQueue, String alteredInput, int id){
        try{statement.executeUpdate(
                "UPDATE trainee SET " + fieldQueue.peek() + " = \'" + alteredInput + "\' WHERE id = " + id
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
