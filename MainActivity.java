package com.example.prosmote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

public class SecActivity extends Activity {
    	
        private SparseArray<String> codes;

        private Object irService;

        private Method irWrite;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
   
       
        
       new SparseArray<String>();
        
       codes.put(
               R.id.button2,
               "0000 0073 0000 000C 0020 0020 0040 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0040 0020 0020 0020 0020 0020 0020 0040 0040 0020 0CA4");
 
       codes.put(
               R.id.button3,
               "0000 0073 0000 000D 0020 0020 0040 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0040 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0020 0CA4");
        
       
       // Get the irda system service
       irService = this.getSystemService("irda");
       // Get the class of the server
       Class c = irService.getClass();
       // Get the string class
       Class p[] = { String.class };
       
       try {
           // Get the method from the service
           irWrite = c.getMethod("write_irsend", p);
   } catch (NoSuchMethodException e) {
           //	 Print the stack trace for no particular reason
           e.printStackTrace();

           // Show a dialog to say something went wrong
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Something broke...");
           builder.setMessage("Failed get the IR send function. Try again maybe?");
           builder.show();
   }
}       
       
   



public void irSend(View view) {

        String data = codes.get(view.getId());

        // Send the command
        irSend(data);
	}	
	
private void irSend(String data) {
        // Make sure one got called
        if (data != null) {
                try {
                        // Convert the code to hex
                        data = hex2dec(data);

                        
                        // Activity the IR port
                        irWrite.invoke(irService, data);
                } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                } catch (IllegalAccessException e) {
                        e.printStackTrace();
                } catch (InvocationTargetException e) {
                        e.printStackTrace();
                }                  
}
} 


private String hex2dec(String irData) {
    // Split the command on ' '
    List<String> list = new ArrayList<String>(Arrays.asList(irData
                    .split(" ")));

    // Remove the dummy data
    list.remove(0);

    // Get the frequency from the list and parse it
    int frequency = Integer.parseInt(list.remove(0), 16); // frequency

    // Remove the next two parts
    list.remove(0); // seq1
    list.remove(0); // seq2

    // Go through each section and parse it to as a base 16 int
    for (int i = 0; i < list.size(); i++) {
            // Set the new section
            list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
    }

    // Calculate the frequency
    frequency = (int) (1000000 / (frequency * 0.241246));

    // Insert the frequency at the start
    list.add(0, Integer.toString(frequency));

    // Reset the ir data string
    irData = "";

    // Add each converted section to return
    for (String s : list) {
            // Add it and separate by comma
            irData += s + ",";
    }

    // Return the hex representation
    return irData;
	
	}
}
