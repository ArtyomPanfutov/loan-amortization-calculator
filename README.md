## Java library for calculating annual loan amortization schedule



```xml
<dependency>
  <groupId>paqua</groupId>
  <artifactId>loan-amortization-calculator</artifactId>
  <version>0.0.7</version>
</dependency>
```


Usage example:
```java
        Loan loan = Loan.builder()
                .amount(BigDecimal.valueOf(500000.32))
                .rate(BigDecimal.valueOf(4.56))
                .term(32)
                .build();
                
        LoanCalculator calculator = new LoanCalculator();

        LoanAmortization amortization = calculator.calculate(loan);

```

Deployed only to github repository
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
