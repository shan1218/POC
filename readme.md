Steps to be followed to send sales message:

1. Start the micro service
2. POST URL : http://localhost:8090/message/sendSalesInfo

	Header:
		Content-Type: application/json
		
		Accept: application/json
		
		
	Body:
	
	
		Sample body without adjustments:
		
		
			{"productName":"orange","quantity":1,"numberOfSales":1}		
			
			
		Sample body with adjustments:
		
		
			{"productName":"orange","quantity":1,"numberOfSales":1,"adjustmentType":"MULTIPLY","adjustmentQuantity":10000}