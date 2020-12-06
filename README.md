## Java library for calculating annual loan amortization schedule



```xml
<dependency>
  <groupId>paqua</groupId>
  <artifactId>loan-amortization-calculator</artifactId>
  <version>1.0.2</version>
</dependency>
```


Usage example:

1. Regular loan without additional payments
```java
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32)) // Debt amount
                .rate(BigDecimal.valueOf(4.56))        // Interest rate
                .term(32)                              // Loan term in MONTHS
                .build();
                
        LoanCalculator calculator = new LoanCalculator();
        LoanAmortization amortization = calculator.calculate(loan);

```
2. Loan with different kinds of addtitional payments 
```java

        // Key - payment number in loan amortization schedule(starts with 0)
        // Value - additional payment attributes
        Map<Integer, EarlyPayment> earlyPayments = new HashMap<>();
        
        earlyPayments.put(3, new EarlyPayment(
                BigDecimal.valueOf(30000),                // Amount of additional payment
                EarlyPaymentStrategy.DECREASE_TERM,       // Strategy of this additional payment that would be applied to the loan
                EarlyPaymentRepeatingStrategy.SINGLE,     // Repeating strategy for this addtional payment 
                null                                      // Additional parameteres (optional)
        ));   
        earlyPayments.put(5, new EarlyPayment(
                BigDecimal.valueOf(50000),
                EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT,
                EarlyPaymentRepeatingStrategy.SINGLE,
                null
        ));

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))    // Loan debt
                .rate(BigDecimal.valueOf(4.56))           // Interest rate
                .earlyPayments(earlyPayments)             // Additional payments
                .term(10)                                 // Loan term in MONTHS
                .build();

        LoanCalculator calculator = new LoanCalculator();
        LoanAmortization amortization = calculator.calculate(loan);

```

Deployed only to github repository. 

Example of maven profile configuration:
```xml
  <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
        <repository>
          <id>github</id>
          <name>GitHub OWNER Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/ArtyomPanfutov/loan-amortization-calculator</url>
        </repository>
      </repositories>
    </profile>
```
