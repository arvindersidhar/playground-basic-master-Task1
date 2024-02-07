import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class SampleClientTest {

	@Test
	public void test() {
		try {
			SampleClient _patient = new SampleClient(); 
			List<SampleClient.PersonObject> _patientist = _patient.dataSucessFullyFetched("Smith", 20);
			assertTrue(!_patientist.isEmpty(), "Sucessfully Fetched");
		}
		catch (ArithmeticException e) {
            // Code to handle the exception
            System.err.println("Error: " + e.getMessage());
        }

		
	}

}
