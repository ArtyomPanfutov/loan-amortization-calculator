# Loan amortization calculator
This is a light-weight library that allows calculating annual loan amortization schedule with a capability of setting early (additional) payments. </br>
The calculation is implemented is in this library without using any extra heavy transitive dependencies.

It is written in Java, thus can be used in any language that have interop with Java.

## How to use?
### Native image
If you want to try it in a serverless container there is a project that provides a native image of a service that is a wrapper for this library. It has one HTTP endpoint for calculating loan amortization.
</br> 
You can find it [here](https://github.com/ArtyomPanfutov/loan-amortization-calculator-service).
### Dependency from Maven Central Repository
Add one of the following the dependencies depending on what package manager you use.
#### Apache Maven
```xml
<dependency>
  <groupId>io.github.ArtyomPanfutov</groupId>
  <artifactId>loan-amortization-calculator</artifactId>
  <version>1.0.9</version>
</dependency>
```

#### Gradle Groovy DSL
```Groovy
implementation 'io.github.ArtyomPanfutov:loan-amortization-calculator:1.0.9'
```

#### Gradle Kotlin DSL
```Kotlin
implementation("io.github.ArtyomPanfutov:loan-amortization-calculator:1.0.9")
```

#### Scala SBT
```Scala
libraryDependencies += "io.github.ArtyomPanfutov" % "loan-amortization-calculator" % "1.0.9"
```

### Usage example:
See the following example of source code to understand the API of the library.

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

### Early payments
Early payments or additional payments to a monthly payment can be passed in the map of early payments in loan object.</br>

In that map a key is an integer that indicates the month number of that payment (numbers start with 0). <br>
The value in the map is an early payment object. </br>

An early payment object consists of amount, early payment type (strategy), repeating strategy, and additional parameters.

#### Early payment type (strategy)
There are two strategies of early payments:
* **DECREASE_TERM** — a payment that decreases term of a loan
* **DECREASE_MONTHLY_PAYMENT** — a payment that decreases amount of a monthly payment

Each early payment has to have either of these strategies. </br>

#### Early payment repeating strategies
An early payment can have a repeating strategy. Basically it allows to copy an early payment to next months.
The strategy is applied automatically before calculation starts. </br>
This is a required parameter and for the payments that should not be repeated a **SINGLE** strategy should be set.

This parameter can have the following values:
* **SINGLE** — a single payment that will not be repeated
* **TO_END** — a payment that will be added to each month starting from the current up to the end of a loan term.
* **TO_CERTAIN_MONTH** — a payment that will be added to each month from the current up to the month specified in additional parameters.
This type of early payment requires passing additional parameter in additional parameters map.

#### Additional parameters
Additional parameters can be passed for an early payment. 
Currently, there is only one special parameter that should be passed for payments with **TO_CERTAIN_MONTH** repeating strategy.
* **REPEAT_TO_MONTH_NUMBER** — a number of month for a payment to be repeated.

### About calculation strategies 
If you pass ```firstPaymentDate``` parameter then the calculatioin strategy for interests will be _**actual/actual**_. That means the actual number of days in a month and in a year will be used to get accrued interest. Therefore, you will get different interest value with provided ```firstPaymentDate```.

### Contribution to the project
If you want to contribute - see [CONTRIBUTING.md](CONTRIBUTING.md)

Pull requests are welcome! 

### License
[MIT](LICENSE)
