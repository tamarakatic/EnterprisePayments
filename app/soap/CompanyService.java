package soap;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.example.service.bankstatement.BankStatement;
import com.example.service.bankstatementrequest.BankStatementRequest;
import com.example.service.paymentorder.PaymentOrder;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface CompanyService {

	@WebMethod
	public BankStatement processBankStatementRequest(BankStatementRequest request);
	
	@WebMethod
	public String processPaymentOrder(PaymentOrder paymentOrder);
	
}
