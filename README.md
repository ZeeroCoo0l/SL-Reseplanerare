# SL-Light Programming Project
## Dataset Description and Overview
I'm worked with a reduced dataset from Stockholm's public transit system (SL), focusing on subway, tram, and inner-city buses (numbers below 100) for a single day (January 31st, 2020). This dataset follows the General Transit Feed Specification (GTFS) standard, which is commonly used for public transit data worldwide.
The dataset consists of several interconnected CSV files representing a normalized relational database structure:

- sl_agency.txt (1 row): Information about the transit operator (SL)
- sl_routes.txt (38 rows): Details about the various transit routes
- sl_stop_times.txt (143,044 rows): Schedule information for arrivals/departures at stops
- sl_stops.txt (443 rows): Transit stop locations and information
- sl_trips.txt (6,679 rows): Individual trips made by vehicles along routes

Worth noting that calendar files are irrelevant for this dataset since it only covers a single day.

## Understanding the Data Structure
The GTFS data follows this hierarchical relationship:

- Transportation agencies (agency) operate routes (routes)
- Routes consist of individual trips (trips)
- Trips comprise a sequence of stop times (stop_times)
- Stop times occur at specific stops/stations (stops)

This dataset allowed me to build a graph-based representation of Stockholm's transit network, with stops as nodes and routes/trips as edges between them.

## Implementation Approach
For my graph implementation, I'll primarily focused on:

- Using stops.txt to create nodes (containing location coordinates)
- Using stop_times.txt to establish connections between stops
- Potentially incorporating additional data from routes.txt and trips.txt for a richer graph model

While using this dataset is optional for the project, it provides a realistic test case with sufficient complexity (443 stops/nodes) to validate my implementation's performance and functionality.

## References

GTFS Overview
GTFS Reference Documentation

_This project is part of the ALDA course at DSV/Stockholm University._
