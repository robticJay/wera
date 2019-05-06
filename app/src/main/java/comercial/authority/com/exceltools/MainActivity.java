package comercial.authority.com.exceltools;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity1234";
    private String[] FilePathStrings,FileNameStrings;
    private File [] listFile;
    File file;

    Button btnUpDirectory,btnSDCard,insert;
    ArrayList<String> pathHistory;
    String lastDirectory;
    int count = 1;

    ArrayList<XYValue> uploadData;
    ListView lvInternalStorage;

    String cellInfo;

    CustomAdapter adapter;
    ListView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvInternalStorage = findViewById(R.id.ivInternalStorage);
        btnUpDirectory =findViewById(R.id.btnUpDirectory);
        btnSDCard = findViewById(R.id.btnViewSDcard);
        uploadData = new ArrayList<>();
        adapter = new CustomAdapter(this,uploadData);
        data = findViewById(R.id.tvData);
        insert = findViewById(R.id.export);


        checkFilePermission();

        lvInternalStorage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastDirectory = pathHistory.get(count);
                if (lastDirectory.equals(parent.getItemAtPosition(position))){
                    Log.d(TAG,"lvInternalStorage: selected a file for upload" +lastDirectory);
                    //execute method for excel data
                    readExcelData(lastDirectory);
                }else{
                    count++;
                    pathHistory.add(count,(String)parent.getItemAtPosition(position));
                    checkInternalStorage();
                    Log.d(TAG,"lvInternalStorage: "+pathHistory.get(count));
                }
            }
        });

        btnUpDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count==0){
                    Log.d(TAG,"btnUPDirectory : you have reached the highest level directory");
                }else {
                    pathHistory.remove(count);
                    count--;
                    checkInternalStorage();
                    Log.d(TAG,"btnUpdirectory :" + pathHistory.get(count));
                }
            }
        });
        btnSDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                pathHistory = new ArrayList<String>();
                pathHistory.add(count,System.getenv("EXTERNAL_STORAGE"));
                Log.d(TAG,"btnSdcard :" + pathHistory.get(count));
                checkInternalStorage();
            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SendingActivity.class);
                startActivity(intent);
            }
        });






    }

    private void readExcelData(String filePath) {
        Log.d(TAG,"reading excel data : reading excel file");
        File inputFile = new File(filePath);
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            for (int r = 1;r < rowsCount; r++){
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //inner loop loops through columns
                for (int c = 0 ; c< cellsCount;c++){
                    if (c>2){
                        Log.e(TAG,"readExcelData :ERROR excel file format is incorrect");
                        toastMessage("readExcelData :ERROR excel file format is incorrect");
                        break;
                    }else {
                        String value = getCellAsString(row,c,formulaEvaluator);
                        String cellInfo = "r:" +r+";c:"+c+"; v:"  + value;
                        Log.d(TAG,"readExcelData : Data fromRow" +cellInfo);
                        sb.append(value +",");
                    }
                }
                sb.append(":");

            }
            Log.d(TAG,"readExcelData : StringBuilder" +sb.toString());

            parseStringBuilder(sb);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseStringBuilder(StringBuilder mStringBuilder) {
        Log.d(TAG,"parseStringBuilder : started");
        //splits the sb into rows
        String[] rows = mStringBuilder.toString().split(":");

        Toast.makeText(this, "DATA COUNT "+rows.length, Toast.LENGTH_SHORT).show();

        //add to the aarraylist<>XYValue> row by row
        for (int i =0;i<rows.length;i++){

            String[] columns = rows[i].split(",");

            //use try catch to make sure that there are no ""trying to parse into doubles

            try {

            double x = Double.parseDouble(columns[0]);
            double y = Double.parseDouble(columns[1]);


             cellInfo = "(x,y):  ("+ x +","+ y +")";
            Log.d(TAG,"parseStringBuilder : data from row"+cellInfo);
            //add the upload data array list



                uploadData.add(new XYValue(x,y));




            }catch (NumberFormatException e){
                Log.d(TAG,"parseStringBuilder : NumberFormatException"+ e.getMessage());

            }
        }



        printDataToLog();
        data.setAdapter(adapter);




    }

    private void printDataToLog() {
        Log.d(TAG," printDataToLog: printing data to log");
        double x,y;

        for (int i = 0;i<uploadData.size();i++){
            x = uploadData.get(i).getX();
            y = uploadData.get(i).getY();
            Log.d(TAG,"printDataToLog x:y ("+ x +","+ y +")");
                    }





    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()){
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                    case Cell.CELL_TYPE_NUMERIC:
                        double numericValue = cellValue.getNumberValue();
                        if (HSSFDateUtil.isCellDateFormatted(cell)){
                            double date = cellValue.getNumberValue();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                            value = formatter.format(HSSFDateUtil.getJavaDate(date));
                        }else {
                            value = ""+numericValue;
                        }
                        break;
                        case Cell.CELL_TYPE_STRING:
                            value = ""+cellValue.getStringValue();
                            break;
                            default:
            }
        }catch (NullPointerException e){
            Log.d(TAG, "getCellAsString:nullpointer Exception" + e.getMessage());
        }
        return value;
    }

    private void checkInternalStorage() {
        Log.d(TAG,"check internal storage started");

        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                toastMessage("no sd card found");
            } else {

                file = new File(pathHistory.get(count));
                Log.d(TAG,"checkInternalStorage" + pathHistory.get(count));


            }
                listFile = file.listFiles();

                FilePathStrings = new String[listFile.length];
                FileNameStrings = new String[listFile.length];

                for (int i = 0; i < listFile.length; i++) {
                    FilePathStrings[i] = listFile[i].getAbsolutePath();
                    FileNameStrings[i] = listFile[i].getName();
                }


                for (int i = 0; i < listFile.length; i++) {

                    Log.d("Files", "Filename:" + listFile[i].getName());

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, FilePathStrings);
                lvInternalStorage.setAdapter(adapter);
        }catch (NullPointerException e){
            Log.d(TAG,"checkInternalStorage : NULLPOINTEREXCEPTION" + e.getMessage());
        }

    }

    private void checkFilePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck+=this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1001);//any number

            }
        }else {
            Log.d(TAG,"checkBT permission :no need to check permission. SDK < lolipop");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }
}
