import googlemaps

gmaps = googlemaps.Client(key='API-KEY REQUIRED HERE')
locations = ["London", "Birmingham", "Manchester", "Leeds", "Sheffield", "Glasgow", "Newcastle upon Tyne",
             "Caerdydd", "Nottingham", "Liverpool", "Southend onSea", "Bristol", "Edinburgh", "Brighton", "Bradford"]

f = open("src/main/resources/data.sql", "a")
f.truncate(0)
f.write("""
TRUNCATE TABLE CITY;
INSERT INTO CITY (NAME, LATITUDE, LONGITUDE) VALUES 
""")

for i, location in enumerate(locations):
    locationLookup = location + ", UK"
    print(locationLookup)

    # Geocoding an address
    geocode_result = gmaps.geocode(locationLookup)

    sqlComma = ","
    if i == len(locations) - 1:
        sqlComma = ""

    long = geocode_result[0]['geometry']['location']['lng']
    lat = geocode_result[0]['geometry']['location']['lat']
    sql = f"('{locations[i]}', '{str(lat)}', '{long}'){sqlComma}\n"
    f.write(sql)
f.close()
