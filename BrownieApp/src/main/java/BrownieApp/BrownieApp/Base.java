package BrownieApp.BrownieApp;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class Base {
	
	


	
	    private static final String APPLICATION_NAME = "Google Sheets to MySQL";
	    private static final String SPREADSHEET_ID = "your-spreadsheet-id"; // Replace with your Google Sheet ID
	    private static final String RANGE = "Sheet1!A1:C"; // Adjust range as needed
	    private static final String JSON_KEY_FILE = "path-to-your-service-account-key.json";

	    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
	    private static final String DB_USER = "your_username";
	    private static final String DB_PASSWORD = "your_password";

	    public static void main(String[] args) throws IOException {
	        // Authorize and build Sheets API client
	        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(JSON_KEY_FILE))
	                .createScoped(SheetsScopes.SPREADSHEETS_READONLY);
	        Sheets sheetsService = new Sheets.Builder(
	                new com.google.api.client.http.javanet.NetHttpTransport(),
	                new com.google.api.client.json.jackson2.JacksonFactory(),
	                new HttpCredentialsAdapter(credentials))
	                .setApplicationName(APPLICATION_NAME)
	                .build();

	        // Read data from Google Sheets
	        ValueRange response = sheetsService.spreadsheets().values()
	                .get(SPREADSHEET_ID, RANGE)
	                .execute();
	        List<List<Object>> values = response.getValues();

	        if (values == null || values.isEmpty()) {
	            System.out.println("No data found in the sheet.");
	            return;
	        }

	        // Insert data into MySQL
	        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	            String insertQuery = "INSERT INTO your_table (column1, column2, column3) VALUES (?, ?, ?)";
	            PreparedStatement stmt = conn.prepareStatement(insertQuery);

	            for (List<Object> row : values) {
	                stmt.setString(1, row.get(0).toString());
	                stmt.setString(2, row.get(1).toString());
	                stmt.setString(3, row.get(2).toString());
	                stmt.addBatch();
	            }
	            stmt.executeBatch();
	            System.out.println("Data inserted into MySQL successfully.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	

}
