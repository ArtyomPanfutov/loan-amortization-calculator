# Java library for calculating annual loan amortization schedule


### Apache Maven
```xml
<dependency>
  <groupId>io.github.ArtyomPanfutov</groupId>
  <artifactId>loan-amortization-calculator</artifactId>
  <version>1.0.8</version>
</dependency>
```

### Gradle Groovy DSL
```Groovy
implementation 'io.github.ArtyomPanfutov:loan-amortization-calculator:1.0.8'
```

### Gradle Kotlin DSL
```Kotlin
implementation("io.github.ArtyomPanfutov:loan-amortization-calculator:1.0.8")
```

### Scala SBT
```Scala
libraryDependencies += "io.github.ArtyomPanfutov" % "loan-amortization-calculator" % "1.0.8"
```

Usage example:

1. A Regular loan without any additional payments
```java
        Loan loan = Loan.builder()
                .amount(500000.39) // Debt amount
                .rate(4.56)        // Interest rate
                .term(32)          // Loan term in MONTHS
                .build();
                
        LoanAmortizationCalculator calculator = LoanAmortizationCalculatorFactory.create();
        LoanAmortization amortization = calculator.calculate(loan);

```
2. A loan with different kinds of addtitional payments 
```java

        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))    // Loan debt
                .rate(BigDecimal.valueOf(4.56))           // Interest rate
                .term(10)                                 // Loan term in MONTHS
                .earlyPayment(3, EarlyPayment.builder()
                    .amount(3500.00)
                    .strategy(EarlyPaymentStrategy.DECREASE_TERM)
                    .repeatingStrategy(EarlyPaymentRepeatingStrategy.TO_CERTAIN_MONTH)  
                    .repeatTo(7)
                    .build())
                .earlyPayment(8, EarlyPayment.builder()
                    .amount(50000.00)
                    .strategy(EarlyPaymentStrategy.DECREASE_MONTHLY_PAYMENT)
                    .repeatingStrategy(EarlyPaymentRepeatingStrategy.SINGLE)
                    .build())
                .build();

        LoanAmortizationCalculator calculator = LoanAmortizationCalculatorFactory.create();
        LoanAmortization amortization = calculator.calculate(loan);

```

### Contribution to the project
If you want to contribute - see [CONTRIBUTING.md](CONTRIBUTING.md)

Pull requests are welcome! 

### License
[MIT](LICENSE)
