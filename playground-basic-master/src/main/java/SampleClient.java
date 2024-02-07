import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.text.SimpleDateFormat;


public class SampleClient extends JFrame {

	public List<PersonObject> dataSucessFullyFetched(String lastName, int pageSize) {
		// Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Set the number of entries per page
        

     // Search for Patient resources
        Bundle response = client
                .search()
                .forResource(Patient.class)
                .count(pageSize)
                .where(Patient.FAMILY.matches().value(lastName))
                .sort().ascending(Patient.GIVEN)
                .returnBundle(Bundle.class)
                .execute()
                ;
        
        List<PersonObject> listObjPerson = new ArrayList<>();
        System.out.println("Patient ID," + "First Name," + "Last Name," + "DOB");
        return processBundle(response, listObjPerson);
        
     // Retrieve subsequent pages if available
        /*
        while (response.getLink(Bundle.LINK_NEXT) != null) {
            response = client.loadPage().next(bundle).execute();
            processBundle(response, listObjPerson);
        }
        */

	}
	
    
    
    private List<PersonObject> processBundle(Bundle response, List<PersonObject> listObjPerson) {
    	List<Bundle.BundleEntryComponent> sortedEntries = response.getEntry()
                .stream()
                .sorted(Comparator.comparing(entry -> getGivenName(entry.getResource())))
                .collect(Collectors.toList());
    	
    	    	
        // Process the entries in the bundle
        for (Bundle.BundleEntryComponent entry : sortedEntries) {
            //Patient patient = (Patient) entry.getResource();
            
            if (entry.getResource() instanceof Patient) {
                Patient patient = (Patient) entry.getResource();
                String patientId = patient.getIdElement().getIdPart();
                String givenName = (patient.getNameFirstRep() != null)? patient.getNameFirstRep().getGivenAsSingleString() : "";
                String familyName = (patient.getNameFirstRep()!= null)? patient.getNameFirstRep().getFamily() : "";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                String dateOfBirth = (patient.getBirthDate() != null)? dateFormat.format(patient.getBirthDate()).toString() : null;
                
                // Print or use the retrieved information as needed
                System.out.println(patientId + "," + givenName + "," + familyName + "," + dateOfBirth);
                
                listObjPerson.add(new PersonObject(patientId, givenName, familyName, dateOfBirth));
                
                
            }
            // Do something with the patient resource
            //System.out.println("Patient Name: " + patient.getIdElement().getIdPart());
            //
        }
        
        return listObjPerson;
    }
    
    private String getGivenName(Resource resource) {
        // Replace this with the actual logic to extract the given name from the resource
        if (resource instanceof Patient) {
            Patient patient = (Patient) resource;
            if (!patient.getName().isEmpty()) {
                HumanName name = patient.getNameFirstRep();
                if (name != null && name.getGiven().size() > 0) {
                    return name.getGiven().get(0).getValue();
                }
            }
        }
        return "";
    }


    
    
    public void GridTableExample(List<PersonObject> personList) {
    	SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Grid Table Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JTable table = createTable(personList);
            JScrollPane scrollPane = new JScrollPane(table);

            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
    
    private JTable createTable(List<PersonObject> personList) {
    	String[] columnNames = {"First Name", "Last Name", "Date Of Birth"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (PersonObject person : personList) {
            Object[] rowData = {person.getFirstName(), person.getLastName(), person.getDateOfBirth()};
            model.addRow(rowData);
        }

        return new JTable(model);
    }

    
    static class PersonObject {
    	private String patientID;
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        

        public PersonObject(String patientID, String firstName, String lastName, String dateOfBirth) {
        	this.patientID = patientID;
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
        }
        
        public String getPatientID() {
            return patientID;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }
    }



}
