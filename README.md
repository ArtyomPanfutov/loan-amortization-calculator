# Java library for calculating annual loan amortization schedule


### Apache Maven
```xml
<dependency>
  <groupId>io.github.ArtyomPanfutov</groupId>
  <artifactId>loan-amortization-calculator</artifactId>
  <version>1.0.5</version>
</dependency>
```

### Gradle Groovy DSL
```Groovy
implementation 'io.github.ArtyomPanfutov:loan-amortization-calculator:1.0.5'
```

### Gradle Kotlin DSL
```Kotlin
implementation("io.github.ArtyomPanfutov:loan-amortization-calculator:1.0.5")
```

### Scala SBT
```Scala
libraryDependencies += "io.github.ArtyomPanfutov" % "loan-amortization-calculator" % "1.0.5"
```

Usage example:

1. A Regular loan without any additional payments
```java
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32)) // Debt amount
                .rate(BigDecimal.valueOf(4.56))        // Interest rate
                .term(32)                              // Loan term in MONTHS
                .build();
                
        LoanAmortizationCalculator calculator = new LoanAmortizationCalculatorImpl();
        LoanAmortization amortization = calculator.calculate(loan);

```
2. A loan with different kinds of addtitional payments 
```java

        // Key - payment number in a loan amortization schedule(starts with 0)
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
                
        LoanAmortizationCalculator calculator = new LoanAmortizationCalculatorImpl();
        LoanAmortization amortization = calculator.calculate(loan);

```

### Contribution to the project
If you want to contribute - see [CONTRIBUTING.md](CONTRIBUTING.md)

Pull requests are welcome! 

### License
[MIT](LICENSE)
