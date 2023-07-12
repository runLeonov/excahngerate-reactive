## regarding the api I am using. It updates the data once a day (thats why, "lastupdated" always shows a time near 00), even for one API key,   the number of requests is very limited,  so I set 1 minute in the master, you can check the logs when the program is running <br> - the data will be updated in a minute
## I decided to use mongo as a database, because the relational model in sql is not suitable for building reactive programs (at least the version I used)

# To run this app you have to have

1. MongoDB server with database "cash" 
2. Recheck if you use default network user in mongo.  
3. Run DemoApplication.java (located in src/main/java/DemoApplication.java) 


# To check program works:
1. Check 1 currency http://localhost:8080/currencies/{currency_code} (exampe: http://localhost:8080/currencies/UAH)<br>
2. Check information about one currency relative to another http://localhost:8080/currencies/{currency_code_to}/rate/{currency_code_from} (exampe: http://localhost:8080/currencies/UAH/rate/USD)<br>
3. Check all currencies http://localhost:8080/currencies

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

await (
await fetch(
'/currencies/USD/rate/UAH',
{
method: 'GET',
headers: {'Content-Type': 'application/json'}
}
)
).json();