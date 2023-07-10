# To run this app you have to have

1. MySQL server with database "cash"
2. Create table in cash db using this script: (script also located in src/main/resources/sql.sql) <br>
   CREATE TABLE currency_rate <br>
   ( <br>
   currency_code VARCHAR(3)  PRIMARY KEY, <br>
   exchange_rate DECIMAL, <br>
   update_time   TIMESTAMP <br>
   ); <br>
3. In application.properties (located in src/main/resources/application.properties) change properties
   spring.r2dbc.url=r2dbc:mysql://localhost:3306/{YOUR_DB} <br>
   spring.r2dbc.username={YOUR_USER} <br>
   spring.r2dbc.password={YOUR_PASSWORD} <br>
4. Run DemoApplication.java (located in src/main/java/DemoApplication.java) 


# To check program works:
1. Check 1 currency http://localhost:8080/currencies/{currency_code} (exampe: http://localhost:8080/currencies/UAH)<br>
2. Check all currency http://localhost:8080/currencies

## I used these scripts to check in browser:
await (
await fetch(
'/currencies',
{
method: 'GET',
headers: {'Content-Type': 'application/json'}
}
)
).json();

await (
await fetch(
'/currencies/UAH',
{
method: 'GET',
headers: {'Content-Type': 'application/json'}
}
)
).json();