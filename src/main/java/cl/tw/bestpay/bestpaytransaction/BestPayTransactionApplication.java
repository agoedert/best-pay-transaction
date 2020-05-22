package cl.tw.bestpay.bestpaytransaction;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import cl.transbank.onepay.Onepay;
import cl.transbank.onepay.exception.AmountException;
import cl.transbank.onepay.exception.SignatureException;
import cl.transbank.onepay.exception.TransactionCreateException;
import cl.transbank.onepay.model.ShoppingCart;
import cl.transbank.onepay.model.Item;
import cl.transbank.onepay.model.TransactionCreateResponse;
import cl.transbank.onepay.model.Transaction;

@SpringBootApplication
@RestController
public class BestPayTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestPayTransactionApplication.class, args);
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, path = "/create-transaction")
	public TransactionResponse hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		Onepay.setCallbackUrl("https://www.misitioweb.com/onepay-result");

		ShoppingCart cart = new ShoppingCart();
		int amount = 1000;

		try {
			cart.add(new Item().setDescription("Zapatos").setQuantity(1).setAmount(amount).setAdditionalData(null)
					.setExpire(-1));
		} catch (AmountException e) {
			e.printStackTrace();
		}

		Onepay.Channel channel = Onepay.Channel.valueOf("WEB");
		TransactionCreateResponse createResponse = null;

		try {
			createResponse = Transaction.create(cart, channel);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (TransactionCreateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new TransactionResponse(amount, createResponse);
	}

	class TransactionResponse {

		private int amount;
		private TransactionCreateResponse createResponse;

		public TransactionResponse(int amount, TransactionCreateResponse createResponse) {
			this.amount = amount;
			this.createResponse = createResponse;
		}

		public int getAmount() {
			return amount;
		}

		public String getOcc() {
			return createResponse.getOcc();
		}

		public long getOtt() {
			return createResponse.getOtt();
		}

		public String getExternalUniqueNumber() {
			return createResponse.getExternalUniqueNumber();
		}

		public long getIssuedAt() {
			return createResponse.getIssuedAt();
		}

		public String getQrCodeAsBase64() {
			return createResponse.getQrCodeAsBase64();
		}

	}

}
