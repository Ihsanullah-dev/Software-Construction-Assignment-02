# Order Billing System – Legacy Code Refactoring

## Student Information

**Name:** Ihsan Ullah

**Registration No:** 24ABSWE0026

**Department:** Software Engineering

**Semester:** 5th Semester

**University:** UET Abbottabad Campus

**Course:** Software Construction

**Instructor:** Engr. Rizwan Shah

**Assignment:** Assignment 02 – Legacy Code Refactoring

---

## 1. Introduction

This project focuses on improving an existing Java-based Order Billing System by refactoring its legacy code. Initially, the system had a large `OrderBillingEngine` class that handled multiple responsibilities, including order validation, discount calculation, tax calculation, payment processing, file logging, and notifications.

Managing all these tasks in one class made the code difficult to understand, maintain, test, and update.

To solve these problems, the system was refactored into smaller and more organized classes. Different responsibilities were separated, suitable design patterns were applied, and JUnit 5 tests were used to check the system's behavior.

## 2. Objectives

The main objectives of this assignment are:

- To understand and identify problems in legacy code.
- To improve code readability and maintainability.
- To divide a large class into smaller classes.
- To apply Object-Oriented Programming principles.
- To implement suitable design patterns.
- To write unit tests using JUnit 5.
- To use Git for version control and track development changes.

## 3. Technologies Used

- **Programming Language:** Java
- **IDE:** Apache NetBeans
- **Build Tool:** Apache Maven
- **Testing Framework:** JUnit 5
- **Version Control:** Git
- **Repository Hosting:** GitHub

## 4. Main Features

The refactored system includes the following features:

- Order validation
- Discount calculation
- Tax calculation
- Multiple payment methods
- Email notifications
- SMS notifications
- Order repository interface
- File logging
- Unit testing

## 5. Code Smells Identified

The original code contained several code quality problems:

1. **Large Class:** One class handled many different responsibilities.
2. **Long Methods:** Some methods contained too much code.
3. **Nested Conditions:** Multiple nested if-else statements made the logic difficult to understand.
4. **Tight Coupling:** Different responsibilities were closely connected.
5. **Difficult Testing:** Testing individual features was difficult because everything was combined in one class.

These problems were reduced through refactoring.

## 6. Refactoring and Design Patterns

The original `OrderBillingEngine` was divided into smaller classes, with each class handling a specific responsibility.

The following design patterns and concepts were applied:

### Strategy Pattern

The Strategy Pattern was used to separate different payment, discount, and notification behaviors. This makes it easier to add or change strategies without modifying the entire system.

### Repository Interface

An `OrderRepository` interface was introduced to separate order-saving operations from the main business logic.

### SOLID Principles

The refactored code follows important Object-Oriented Design principles, including:

- **Single Responsibility Principle (SRP):** Each class has a specific responsibility.
- **Open/Closed Principle (OCP):** The system can be extended without unnecessarily changing existing code.
- **Dependency Inversion Principle (DIP):** Abstractions are used to reduce direct dependencies.

## 7. Testing

JUnit 5 was used to test the refactored system.

Two main test classes were created:

### OrderBillingEngineDiscountTest

This class tests discount and tax calculations, including different countries and boundary conditions.

### OrderBillingEngineBehaviorTest

This class tests order validation, payment methods, notification behavior, and file logging.

The tests were written to check the existing system's behavior and help ensure that refactoring did not break its functionality.

### Test Results

| Description | Result |
|---|---|
| Total Test Cases | 56 |
| Failures | 0 |
| Errors | 0 |
| Status | Passed |

## 8. Before and After Comparison

| Metric | Before Refactoring | After Refactoring |
|---|---|---|
| Main Class Lines | 157 | 102 |
| Classes / Interfaces | 1 | 14 |
| Nested If-Else | 4 | 0 |
| Unit Tests | 0 | 56 |
| Longest Method | Approximately 130 lines | Approximately 70–73 lines |

The comparison shows that the code became more organized and easier to maintain after refactoring.

## 9. Git Version Control

Git was used to track the development process and maintain a history of changes.

The project contains multiple commits showing the different stages of development.

To view the commit history, use:

```bash
git log --oneline
```

This command displays the project's commit history in a short format.

## 10. How to Run the Project and Tests

Follow these steps to configure Java, open the project, and run the JUnit 5 test cases using Windows Command Prompt.

### Step 1: Set JAVA_HOME

First, set the `JAVA_HOME` environment variable to the JDK installed with Apache NetBeans.

Open Command Prompt and execute:

```cmd
set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
```

### Step 2: Configure Java PATH

Add the Java `bin` directory to the system PATH for the current Command Prompt session.

```cmd
set "PATH=%JAVA_HOME%\bin;%PATH%"
```

### Step 3: Verify Java Installation

Check whether Java is properly configured by executing:

```cmd
java -version
```

If the Java version is displayed successfully, Java is configured correctly.

### Step 4: Open the Project Directory

Navigate to the Assignment 02 project folder located on the E drive.

```cmd
cd /d "E:\5th Semester Data\Software Construction And Development\Assignment\Assignment 02\Assignment02\OrderBillingRefactor"
```

### Step 5: Run Maven Tests

Execute the following command to clean previous build files, compile the project, and run all JUnit 5 test cases.

```cmd
"C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" clean test
```

### Step 6: Check Test Results

After Maven completes execution, check the test results in Command Prompt.

The expected test summary is:

- **Total Test Cases:** 56
- **Failures:** 0
- **Errors:** 0
- **Status:** BUILD SUCCESS

If the build is successful, all configured tests have passed.

**Note:** Make sure the project directory and Java installation paths are correct before executing these commands.

## 11. Conclusion

In this assignment, the legacy Order Billing System was successfully refactored into a more organized and maintainable structure. The large class was divided into smaller classes, design patterns were applied, and JUnit 5 tests were used to verify the system's behavior.

This assignment helped improve my understanding of legacy code refactoring, Object-Oriented Programming, SOLID principles, design patterns, unit testing, and Git version control.
