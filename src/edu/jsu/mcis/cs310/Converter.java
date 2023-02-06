package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import static java.util.Spliterators.iterator;


public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
         String result = "{}"; // default return value; replace later!
        
        try {
        
            // INSERT YOUR CODE HERE
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> rows = reader.readAll();
            String[] headers = rows.get(0);
            
            
            
            JsonArray colheadings = new JsonArray();
            JsonArray pnumber = new JsonArray();
            JsonArray dataall = new JsonArray();
            
            JsonObject json= new JsonObject();
            
            for (int j = 0; j < headers.length ; j++) {
                colheadings.add(headers[j]); //add the arrays to an array
            }

            for (int i = 1; i<rows.size(); i++) {
                
                String[] values = rows.get(i); //get the row data inside the array
                
                pnumber.add(values[0]); //add the product number to array
                
                JsonArray data = new JsonArray();
                
                for(int k=1; k<values.length;k++){
                    
                    if (k == colheadings.indexOf("Season") || k == colheadings.indexOf("Episode")) {
                        data.add(Integer.valueOf(values[k])); //add the data values to an array
                    }
                    else {
                        data.add(values[k]); //add the data values to an array
                    }
                    
                }
                
                dataall.add(data);
              
            }
 
            json.put("ProdNums", pnumber);
            json.put("ColHeadings", colheadings);
            json.put("Data", dataall);
                
            //System.out.println(colheadings);
            result = Jsoner.serialize(json);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        DecimalFormat decimalFormat = new DecimalFormat("00");
        
        try {
            
            // INSERT YOUR CODE HERE
           
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
            
            
            JsonArray colheadings = new JsonArray();
            colheadings=(JsonArray) (jsonObject.get("ColHeadings"));
            //System.out.println(colheadings);
            
            JsonArray pnumber = new JsonArray();
            pnumber=(JsonArray) (jsonObject.get("ProdNums"));
            //System.out.println(pnumber);
            
            JsonArray dataall = new JsonArray();
            dataall=(JsonArray) (jsonObject.get("Data"));
            //System.out.println(dataall);
            
            
                        
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter, ',', '"', '\\', "\n");
   
            
            // insert headings
            String[] headings = new String[colheadings.size()];
            for (int i = 0; i < colheadings.size(); i++) {
                headings[i] = colheadings.get(i).toString();
            }
            csvWriter.writeNext(headings);
            
            
            //print the data and product number
            for(int i=0;i<pnumber.size();i++){
                String[] row= new  String[colheadings.size()];
                JsonArray data = new JsonArray(); 
                data=(JsonArray) dataall.get(i);
                
                //adding the pnumebr and data to the writer
                row[0]=pnumber.get(i).toString();
                for (int j = 0; j < data.size(); j++) {
                    //System.out.println(data.get(colheadings.indexOf("Episode")-1));
                    if(data.get(j)==data.get(colheadings.indexOf("Episode")-1)){
                        
                        //System.out.println(data.get(colheadings.indexOf("Episode")-1)=="1");                        
                        int number = Integer.parseInt(data.get(j).toString());
                        String formattedNumber = "";
                        
                        formattedNumber = decimalFormat.format(number);
                        
                        //System.out.println(formattedNumber);
                        row[j+1]=formattedNumber;
                    }
                    else{
                        row[j+1] = data.get(j).toString();
                    }
                    
                }
                
                csvWriter.writeNext(row);
                
                
            }
            //System.out.println(pnumber.get(0));
            
            result = stringWriter.toString();
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
        
    }
    
}