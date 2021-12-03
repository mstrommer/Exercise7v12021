package at.ac.fhcampuswien;

import at.ac.fhcampuswien.finance.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(2)
class AppTest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream bos;
    private PrintStream ps;

    @BeforeAll
    public static void init() {
        System.out.println("Testing Exercise 6");
    }

    @AfterAll
    public static void finish() {
        System.out.println("Finished Testing Exercise 6");
    }

    @BeforeEach
    public void setupStreams() throws IOException {
        originalOut = System.out;
        originalIn = System.in;

        bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        System.setIn(pis);
        ps = new PrintStream(pos, true);
    }

    @AfterEach
    public void tearDownStreams() {
        // undo the binding in System
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // check class member
    @Test
    public void classBankAccount1() {
        try {
            // check if there are already fields declared
            assertTrue(BankAccount.class.getDeclaredFields().length != 0, "Class Person hasn't declared any members yet.");
            // check if all fields are named correctly, private,...
            Field balance = BankAccount.class.getDeclaredField("balance");
            assertTrue(Modifier.toString(balance.getModifiers()).equals("private") && balance.getType().toString().equals("double"), "Please check your field names and modifiers!");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // check if isAbstract
    @Test
    public void classBankAccount2() {
        try {
            assertTrue(Modifier.isAbstract(BankAccount.class.getModifiers()), "This class should be abstract. What does this mean? Recommended reading: https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // check constructors exist
    @Test
    public void classBankAccount3() {
        try {
            // check correct headers
            assertEquals(1,
                    Arrays.stream(BankAccount.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.finance.BankAccount()")).count(),
                    "Default Constructor () missing.");
            assertEquals(1,
                    Arrays.stream(BankAccount.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.finance.BankAccount(double)")).count(),
                    "Constructor (double) missing.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void deposit() {
        try {
            Method m = BankAccount.class.getMethod("deposit", double.class);
            Constructor<?> co = SavingsAccount.class.getConstructor(double.class);
            SavingsAccount p = (SavingsAccount) co.newInstance(5.5);
            Field balance = BankAccount.class.getDeclaredField("balance");
            balance.setAccessible(true);
            m.invoke(p,2000.0);
            assertEquals(2000.0, balance.get(p), "Method deposit not working correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("No worries for now. This method can only be tested if class SavingsAccount provides a constructor.");
        }
    }

    @Test
    public void withdraw() {
        try {
            Method m = BankAccount.class.getMethod("withdraw", double.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(2000.6);
            Field balance = BankAccount.class.getDeclaredField("balance");
            balance.setAccessible(true);
            m.invoke(p,100.0);
            assertEquals(1900.6, balance.get(p), "Method withdraw not working correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("No worries for now. This method can only be tested if class CheckingAccount provides a constructor.");
        }
    }

    @Test
    public void getBalance() {
        try {
            Method m = BankAccount.class.getMethod("getBalance");
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(500.5);
            assertEquals(500.5, m.invoke(p), "Method getBalance not working correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("No worries for now. This method can only be tested if class CheckingAccount provides a constructor.");
        }
    }

    @Test
    public void calculateFees() {
        try {
            Method m = BankAccount.class.getMethod("calculateFees");
            assertTrue(Modifier.isAbstract(m.getModifiers()), "This Method should be abstract. Please check https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method calculateFees missing or invalid parameters.");
        }
    }

    @Test
    public void bankAccountToString() {
        try {
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(1000.5);
            String expected = "CheckingAccount : 1000.5";
            assertEquals(expected, p.toString().replaceAll(",","."), "toString Format not correct.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("No worries for now. This method can only be tested if class CheckingAccount provides a constructor.");
        }
    }

    // check class member & inheritance
    @Test
    public void classSavingsAccount1() {
        try {
            // check if there are already fields declared
            assertTrue(SavingsAccount.class.getDeclaredFields().length != 0, "Class Person hasn't declared any members yet.");
            // check if all fields are named correctly, private,...
            Field balance = SavingsAccount.class.getDeclaredField("interestRate");
            assertTrue(Modifier.toString(balance.getModifiers()).equals("private") && balance.getType().toString().equals("double"), "Please check your field names and modifiers!");
            // check inheritance
            assertTrue(BankAccount.class.isAssignableFrom(SavingsAccount.class), "SavingsAccount must inherit from BankAccount.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // check constructor exists and works
    @Test
    public void classSavingsAccount2() {
        try {
            assertEquals(1,
                    Arrays.stream(SavingsAccount.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.finance.SavingsAccount(double)")).count(),
                    "Constructor (double) missing.");
            Constructor<?> co = SavingsAccount.class.getConstructor(double.class);
            SavingsAccount p = (SavingsAccount) co.newInstance(5.5);
            Field rate = SavingsAccount.class.getDeclaredField("interestRate");
            rate.setAccessible(true);
            assertEquals(5.5, (double) rate.get(p), "Constructor doesn't set the interestRate.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void calculateFeesSavingsAccount() {
        try {
            Method m = SavingsAccount.class.getMethod("calculateFees");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method calculateFees missing or invalid parameters.");
        }
    }

    @Test
    public void calculateInterest() {
        try {
            Method m = SavingsAccount.class.getMethod("calculateInterest");
            Constructor<?> co = SavingsAccount.class.getConstructor(double.class);
            SavingsAccount p = (SavingsAccount) co.newInstance(2.5);
            Field balance = BankAccount.class.getDeclaredField("balance");
            balance.setAccessible(true);
            m.invoke(p);
            assertEquals(0.0, (double) balance.get(p), "Interst not computed correctly.");
            balance.set(p, 2000);
            m.invoke(p);
            assertEquals(2050.0, (double) balance.get(p), "Interst not computed correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method calculateInterest missing or invalid parameters.");
        }
    }

    @Test
    public void interestRateGetterSetter() {
        try {
            Method getter = SavingsAccount.class.getMethod("getInterestRate");
            Method setter = SavingsAccount.class.getMethod("setInterestRate", double.class);
            Constructor<?> co = SavingsAccount.class.getConstructor(double.class);
            SavingsAccount p = (SavingsAccount) co.newInstance(3.5);
            Field rate = SavingsAccount.class.getDeclaredField("interestRate");
            rate.setAccessible(true);
            assertEquals(3.5, (double) getter.invoke(p), "Getter not working.");
            setter.invoke(p, 5.5);
            assertEquals(5.5, (double) rate.get(p), "Setter not working.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("InterestRate Getter and/or Setter missing.");
        }
    }

    // check class member & inheritance
    @Test
    public void classCheckingAccount1() {
        try {
            // check if there are already fields declared
            assertTrue(CheckingAccount.class.getDeclaredFields().length != 0, "Class Person hasn't declared any members yet.");
            // check if all fields are named correctly, private,...
            Field transactions = CheckingAccount.class.getDeclaredField("transactions");
            assertTrue(Modifier.toString(transactions.getModifiers()).equals("private") &&
                    transactions.getType().toString().equals("int"), "Please check your field names and modifiers for transactions!");
            Field credits = CheckingAccount.class.getDeclaredField("TRANSACTION_CREDITS");
            assertTrue(Modifier.toString(credits.getModifiers()).equals("private static final") &&
                    credits.getType().toString().equals("int"), "Please check your field names and modifiers for TRANSACTION_CREDITS!");
            Field cost = CheckingAccount.class.getDeclaredField("TRANSACTION_COST");
            assertTrue(Modifier.toString(cost.getModifiers()).equals("private static final") &&
                    cost.getType().toString().equals("double"), "Please check your field names and modifiers for TRANSACTION_COST!");
            // check inheritance
            assertTrue(BankAccount.class.isAssignableFrom(CheckingAccount.class), "CheckingAccount must inherit from BankAccount.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // check constructor exists and works
    @Test
    public void classCheckingAccount2() {
        try {
            assertEquals(1,
                    Arrays.stream(CheckingAccount.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.finance.CheckingAccount(double)")).count(),
                    "Constructor (double) missing.");
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(20000.0);
            Field balance = BankAccount.class.getDeclaredField("balance");
            balance.setAccessible(true);
            assertEquals(20000.0, (double) balance.get(p), "Constructor doesn't set the initial balance correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void depositCheckingAccount() {
        try {
            Method m = BankAccount.class.getMethod("deposit", double.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(1500.0);
            Field balance = BankAccount.class.getDeclaredField("balance");
            Field transactions = CheckingAccount.class.getDeclaredField("transactions");
            balance.setAccessible(true);
            transactions.setAccessible(true);
            m.invoke(p,200.5);
            assertEquals(1700.5, balance.get(p), "Method deposit in CheckingAccount not working correctly.");
            m.invoke(p,200.5);
            assertEquals(2, transactions.get(p), "Method deposit in CheckingAccount not working correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("No worries for now. This method can only be tested if class SavingsAccount provides a constructor.");
        }
    }

    @Test
    public void withdrawCheckingAccount() {
        try {
            Method m = BankAccount.class.getMethod("withdraw", double.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(1500.0);
            Field balance = BankAccount.class.getDeclaredField("balance");
            Field transactions = CheckingAccount.class.getDeclaredField("transactions");
            balance.setAccessible(true);
            transactions.setAccessible(true);
            m.invoke(p,200.5);
            assertEquals(1299.5, balance.get(p), "Method withdraw in CheckingAccount not working correctly.");
            m.invoke(p,200.5);
            assertEquals(2, transactions.get(p), "Method withdraw in CheckingAccount not working correctly.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("No worries for now. This method can only be tested if class SavingsAccount provides a constructor.");
        }
    }

    @Test
    public void calculateFeesCheckingAccount() {
        try {
            Method c = CheckingAccount.class.getMethod("calculateFees");
            Method w = BankAccount.class.getMethod("withdraw", double.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p = (CheckingAccount) co.newInstance(100.0);
            Field transactions = CheckingAccount.class.getDeclaredField("transactions");
            Field balance = BankAccount.class.getDeclaredField("balance");
            transactions.setAccessible(true);
            balance.setAccessible(true);
            for(int i = 0; i < 30; i++) {
                w.invoke(p, 1);
            }
            assertEquals(30, (int) transactions.get(p), "Transactions not computed correctly.");
            c.invoke(p);
            assertEquals(32.5, (double) balance.get(p), "Method calculateFees in CheckingAccount not working correctly.");


        } catch (Exception e) {
            e.printStackTrace();
            fail("Method calculateFees missing or invalid parameters.");
        }
    }

    @Test
    public void transferTransferBehaviour() {
        try {
            Method m = TransferBehaviour.class.getMethod("transfer", double.class, BankAccount.class, BankAccount.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method transfer in Interface TransferBehaviour missing or invalid parameters.");
        }
    }

    @Test
    public void transferRegularTransfer1() {
        try {
            // check inheritance
            assertTrue(TransferBehaviour.class.isAssignableFrom(RegularTransfer.class), "RegularTransfer needs its corresponding Interface.");
            Method m = RegularTransfer.class.getMethod("transfer", double.class, BankAccount.class, BankAccount.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p1 = (CheckingAccount) co.newInstance(1000.0);
            CheckingAccount p2 = (CheckingAccount) co.newInstance(500.0);
            co = RegularTransfer.class.getConstructor();
            RegularTransfer tns = (RegularTransfer) co.newInstance();
            m.invoke(tns, 500.0, p1, p1);
            String expected = "Please provide a valid account payee." + System.lineSeparator();
            assertEquals(expected, bos.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method transfer in RegularTransfer missing or invalid parameters.");
        }
    }

    @Test
    public void transferRegularTransfer2() {
        try {
            // check inheritance
            assertTrue(TransferBehaviour.class.isAssignableFrom(RegularTransfer.class), "RegularTransfer needs its corresponding Interface.");
            Method m = RegularTransfer.class.getMethod("transfer", double.class, BankAccount.class, BankAccount.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p1 = (CheckingAccount) co.newInstance(1000.0);
            CheckingAccount p2 = (CheckingAccount) co.newInstance(500.0);
            co = RegularTransfer.class.getConstructor();
            RegularTransfer tns = (RegularTransfer) co.newInstance();
            m.invoke(tns, 1500.0, p1, p2);
            String expected = "Insufficient funds. Transaction aborted." + System.lineSeparator();
            assertEquals(expected, bos.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method transfer in RegularTransfer missing or invalid parameters.");
        }
    }

    @Test
    public void transferRegularTransfer3() {
        try {
            // check inheritance
            assertTrue(TransferBehaviour.class.isAssignableFrom(RegularTransfer.class), "RegularTransfer needs its corresponding Interface.");
            Method m = RegularTransfer.class.getMethod("transfer", double.class, BankAccount.class, BankAccount.class);
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p1 = (CheckingAccount) co.newInstance(1000.0);
            CheckingAccount p2 = (CheckingAccount) co.newInstance(500.0);

            co = RegularTransfer.class.getConstructor();
            RegularTransfer tns = (RegularTransfer) co.newInstance();

            m.invoke(tns, 200.0, p1, p2);

            Field balance = BankAccount.class.getDeclaredField("balance");
            balance.setAccessible(true);

            assertEquals(800.0, (double) balance.get(p1));
            assertEquals(700.0, (double) balance.get(p2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method transfer in TransferNotSupported missing or invalid parameters.");
        }
    }

    @Test
    public void transferTransferNotSupported() {
        try {
            // check inheritance
            assertTrue(TransferBehaviour.class.isAssignableFrom(TransferNotSupported.class), "TransferNotSupported needs its corresponding Interface.");
            Method m = TransferNotSupported.class.getMethod("transfer", double.class, BankAccount.class, BankAccount.class);
            Constructor<?> co = SavingsAccount.class.getConstructor(double.class);
            SavingsAccount p = (SavingsAccount) co.newInstance(5.0);
            co = TransferNotSupported.class.getConstructor();
            TransferNotSupported tns = (TransferNotSupported) co.newInstance();
            m.invoke(tns, 500.0, p, p);
            String expected = "Transfer not supported for Bank Account Type: SavingsAccount." + System.lineSeparator();
            assertEquals(expected, bos.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method transfer in TransferNotSupported missing or invalid parameters.");
        }
    }

    @Test
    public void performTransfer() {
        try {
            // check member transferBehaviour in BankAccount
            Field transferBehaviour = BankAccount.class.getDeclaredField("transferBehaviour");
            assertTrue(Modifier.toString(transferBehaviour.getModifiers()).equals("protected") &&
                    transferBehaviour.getType().toString().equals(TransferBehaviour.class.toString()), "Please check your field names and modifiers for transferBehaviour!");

            // check method in BankAccount
            Method performTransfer = BankAccount.class.getMethod("performTransfer", double.class, BankAccount.class);
            assertEquals("public final", Modifier.toString(performTransfer.getModifiers()), "performTransfer");

            // create objects
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p1 = (CheckingAccount) co.newInstance(1000.0);
            CheckingAccount p2 = (CheckingAccount) co.newInstance(500.0);

            performTransfer.invoke(p2, 200.0, p1);

            Field balance = BankAccount.class.getDeclaredField("balance");
            balance.setAccessible(true);

            assertEquals(1200.0, (double) balance.get(p1));
            assertEquals(300.0, (double) balance.get(p2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method performTransfer in BankAccount missing or invalid parameters.");
        }
    }

    @Test
    public void checkTransferBehaviour() {
        try {
            // check member transferBehaviour in BankAccount
            Field transferBehaviour = BankAccount.class.getDeclaredField("transferBehaviour");
            transferBehaviour.setAccessible(true);

            // create objects
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p1 = (CheckingAccount) co.newInstance(1000.0);
            co = SavingsAccount.class.getConstructor(double.class);
            SavingsAccount p2 = (SavingsAccount) co.newInstance(5.0);

            assertEquals("RegularTransfer", transferBehaviour.get(p1).getClass().getSimpleName());
            assertEquals("TransferNotSupported", transferBehaviour.get(p2).getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void compareTo() {
        try {
            // check inheritance
            assertTrue(Comparable.class.isAssignableFrom(BankAccount.class),
                    "Make use of Interface Comparable<T> in class BankAccount. See https://docs.oracle.com/javase/tutorial/collections/interfaces/order.html");

            // check method exists
            Method compareTo = BankAccount.class.getMethod("compareTo", BankAccount.class);

            // create objects
            Constructor<?> co = CheckingAccount.class.getConstructor(double.class);
            CheckingAccount p1 = (CheckingAccount) co.newInstance(1000.0);
            CheckingAccount p2 = (CheckingAccount) co.newInstance(1001.0);
            CheckingAccount p3 = (CheckingAccount) co.newInstance(1000.0);
            System.out.println(compareTo.invoke(p2,p1));
            // assertions
            assertTrue((int) compareTo.invoke(p1,p2) < 0, "Check your implementation of compareTo");
            assertTrue((int) compareTo.invoke(p2,p1) > 0, "Check your implementation of compareTo");
            assertTrue((int) compareTo.invoke(p1,p3) == 0, "Check your implementation of compareTo");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // check class member
    @Test
    public void classGeorgina() {
        try {
            // check if there are already fields declared
            assertTrue(Georgina.class.getDeclaredFields().length != 0, "Class Georgina hasn't declared any members yet.");
            // check if all fields are named correctly, private,...
            Field accountList = Georgina.class.getDeclaredField("accountList");
            assertTrue(Modifier.toString(accountList.getModifiers()).equals("private") &&
                    accountList.getType().toString().equals(List.class.toString()), "Please check your field names and modifiers for accountList!");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addAccount() {
        try {
            // check method exists
            Method addAccount = Georgina.class.getMethod("addAccount", BankAccount.class);

            // access private fields
            Field accountList = Georgina.class.getDeclaredField("accountList");
            accountList.setAccessible(true);

            // create objects
            Constructor<?> co1 = CheckingAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            CheckingAccount p1 = (CheckingAccount) co1.newInstance(100.0);
            CheckingAccount p2 = (CheckingAccount) co1.newInstance(101.0);
            CheckingAccount p3 = (CheckingAccount) co1.newInstance(200.0);
            Georgina g = (Georgina) co2.newInstance();

            addAccount.invoke(g, p1);
            addAccount.invoke(g, p1);

            // assertions
            assertEquals("Account already managed by Georgina." + System.lineSeparator(), bos.toString());
            assertEquals(1, ((List<BankAccount>) accountList.get(g)).size());
            addAccount.invoke(g, p2);
            addAccount.invoke(g, p3);
            assertEquals(3, ((List<BankAccount>) accountList.get(g)).size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Method addAccount missing in class Georgina. Also check parameters.");
        }
    }

    @Test
    public void printAccounts1() {
        try {
            // check method exists
            Method addAccount = Georgina.class.getMethod("addAccount", BankAccount.class);
            Method printAccounts = Georgina.class.getMethod("printAccounts", boolean.class);

            // create objects
            Constructor<?> co1 = CheckingAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            CheckingAccount p1 = (CheckingAccount) co1.newInstance(100.0);
            CheckingAccount p2 = (CheckingAccount) co1.newInstance(101.0);
            CheckingAccount p3 = (CheckingAccount) co1.newInstance(202.0);
            CheckingAccount p4 = (CheckingAccount) co1.newInstance(500.0);
            CheckingAccount p5 = (CheckingAccount) co1.newInstance(80.0);
            Georgina g = (Georgina) co2.newInstance();

            addAccount.invoke(g, p1);
            addAccount.invoke(g, p2);
            addAccount.invoke(g, p3);
            addAccount.invoke(g, p4);
            addAccount.invoke(g, p5);
            printAccounts.invoke(g,true);

            // expected
            String expected = "CheckingAccount : 80.0" + System.lineSeparator() +
                    "CheckingAccount : 100.0" + System.lineSeparator() +
                    "CheckingAccount : 101.0" + System.lineSeparator() +
                    "CheckingAccount : 202.0" + System.lineSeparator() +
                    "CheckingAccount : 500.0" + System.lineSeparator();

            // assertions
            assertEquals(expected, bos.toString().replaceAll(",","."));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Method printAccounts missing in class Georgina. Also check parameters.");
        }
    }

    @Test
    public void printAccounts2() {
        try {
            // check method exists
            Method addAccount = Georgina.class.getMethod("addAccount", BankAccount.class);
            Method printAccounts = Georgina.class.getMethod("printAccounts", boolean.class);

            // create objects
            Constructor<?> co1 = CheckingAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            CheckingAccount p1 = (CheckingAccount) co1.newInstance(100.0);
            CheckingAccount p2 = (CheckingAccount) co1.newInstance(101.0);
            CheckingAccount p3 = (CheckingAccount) co1.newInstance(202.0);
            CheckingAccount p4 = (CheckingAccount) co1.newInstance(500.0);
            CheckingAccount p5 = (CheckingAccount) co1.newInstance(80.0);
            Georgina g = (Georgina) co2.newInstance();

            addAccount.invoke(g, p1);
            addAccount.invoke(g, p2);
            addAccount.invoke(g, p3);
            addAccount.invoke(g, p4);
            addAccount.invoke(g, p5);
            printAccounts.invoke(g,false);

            // expected
            String expected = "CheckingAccount : 500.0" + System.lineSeparator() +
                    "CheckingAccount : 202.0" + System.lineSeparator() +
                    "CheckingAccount : 101.0" + System.lineSeparator() +
                    "CheckingAccount : 100.0" + System.lineSeparator() +
                    "CheckingAccount : 80.0" + System.lineSeparator();

            // assertions
            assertEquals(expected, bos.toString().replaceAll(",","."));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Method printAccounts missing in class Georgina. Also check parameters.");
        }
    }

    @Test
    public void simulateFinancialProspect1() {
        try {
            // check method exists
            Method simulateFinancialProspect = Georgina.class.getMethod("simulateFinancialProspect", BankAccount.class, int.class);
            Method getBalance = BankAccount.class.getMethod("getBalance");

            // create objects
            Constructor<?> co1 = SavingsAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            SavingsAccount p1 = (SavingsAccount) co1.newInstance(5.0);
            Georgina g = (Georgina) co2.newInstance();
            double balance = (double) getBalance.invoke(p1);
            simulateFinancialProspect.invoke(g,p1,4);

            // expected
            String expected = "Your balance after 4 years is expected to be 0.0" + System.lineSeparator();

            // assertions
            assertEquals((double) getBalance.invoke(p1), balance, "Balance may not be changed permanently during the simulation. The use of setBalance is not allowed.");
            assertEquals(expected, bos.toString().replaceAll(",","."));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Method simulateFinancialProspect missing in class Georgina. Also check parameters.");
        }
    }

    // normal years
    @Test
    public void simulateFinancialProspect2() {
        try {
            // check method exists
            Method simulateFinancialProspect = Georgina.class.getMethod("simulateFinancialProspect", BankAccount.class, int.class);
            Method deposit = SavingsAccount.class.getMethod("deposit", double.class);

            // create objects
            Constructor<?> co1 = SavingsAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            SavingsAccount p1 = (SavingsAccount) co1.newInstance(5.0);
            Georgina g = (Georgina) co2.newInstance();
            deposit.invoke(p1,2000.0);

            simulateFinancialProspect.invoke(g,p1,4);

            // expected
            String expected = "Your balance after 4 years is expected to be 2431" + System.lineSeparator();

            // assertions
            assertEquals(expected, bos.toString().replaceAll(",",".").replaceAll("\\..*$", ""));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Method simulateFinancialProspect missing in class Georgina. Also check parameters.");
        }
    }

    // with crash years
    @Test
    public void simulateFinancialProspect3() {
        try {
            // check method exists
            Method simulateFinancialProspect = Georgina.class.getMethod("simulateFinancialProspect", BankAccount.class, int.class);
            Method deposit = SavingsAccount.class.getMethod("deposit", double.class);

            // create objects
            Constructor<?> co1 = SavingsAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            SavingsAccount p1 = (SavingsAccount) co1.newInstance(5.0);
            Georgina g = (Georgina) co2.newInstance();
            deposit.invoke(p1,2000.0);

            simulateFinancialProspect.invoke(g,p1,15);

            // expected
            String expected = "Your balance after 15 years is expected to be 3867" + System.lineSeparator();

            // assertions
            assertEquals(expected, bos.toString().replaceAll(",",".").replaceAll("\\..*$", ""));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Method simulateFinancialProspect missing in class Georgina. Also check parameters.");
        }
    }

    // requirement of Savingsaccount
    @Test
    public void simulateFinancialProspect4() {
        try {
            // check method exists
            Method simulateFinancialProspect = Georgina.class.getMethod("simulateFinancialProspect", BankAccount.class, int.class);

            // create objects
            Constructor<?> co1 = CheckingAccount.class.getConstructor(double.class);
            Constructor<?> co2 = Georgina.class.getConstructor();
            CheckingAccount p1 = (CheckingAccount) co1.newInstance(500.0);
            Georgina g = (Georgina) co2.newInstance();

            simulateFinancialProspect.invoke(g,p1,15);

            // expected
            String expected = "Financial prospects require a Savings Account." + System.lineSeparator();

            // assertions
            assertEquals(expected, bos.toString());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Method simulateFinancialProspect missing in class Georgina. Also check parameters.");
        }
    }
}