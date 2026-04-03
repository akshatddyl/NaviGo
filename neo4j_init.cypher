// =========================================================================
// NaviGo — Neo4j AuraDB Initialization Script
// Run these Cypher queries in the Neo4j AuraDB Browser before using the app
// =========================================================================

// Step 1: Create constraints
CREATE CONSTRAINT location_id IF NOT EXISTS FOR (l:Location) REQUIRE l.id IS UNIQUE;

// Step 2: Create the demo venue (Graphic Era Hill University — Demo Floor)
// 25 nodes, all on floor 0

CREATE (entrance:Location {id:'node_01', name:'Main Entrance', floor:0, venueId:'demo_venue', accessible:true, type:'entrance'})
CREATE (lobby:Location {id:'node_02', name:'Main Lobby', floor:0, venueId:'demo_venue', accessible:true, type:'junction'})
CREATE (reception:Location {id:'node_03', name:'Reception Desk', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (lift:Location {id:'node_04', name:'Elevator', floor:0, venueId:'demo_venue', accessible:true, type:'elevator'})
CREATE (corridor_a:Location {id:'node_05', name:'Corridor Junction A', floor:0, venueId:'demo_venue', accessible:true, type:'junction'})
CREATE (corridor_b:Location {id:'node_06', name:'Corridor Junction B', floor:0, venueId:'demo_venue', accessible:true, type:'junction'})
CREATE (room_101:Location {id:'node_07', name:'Room 101', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (room_102:Location {id:'node_08', name:'Room 102', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (room_103:Location {id:'node_09', name:'Room 103', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (toilet_m:Location {id:'node_10', name:'Male Restroom', floor:0, venueId:'demo_venue', accessible:true, type:'toilet'})
CREATE (toilet_f:Location {id:'node_11', name:'Female Restroom', floor:0, venueId:'demo_venue', accessible:true, type:'toilet'})
CREATE (canteen:Location {id:'node_12', name:'Canteen', floor:0, venueId:'demo_venue', accessible:true, type:'canteen'})
CREATE (stairs:Location {id:'node_13', name:'Staircase', floor:0, venueId:'demo_venue', accessible:false, type:'staircase'})
CREATE (exit_rear:Location {id:'node_14', name:'Rear Exit', floor:0, venueId:'demo_venue', accessible:true, type:'exit'})
CREATE (library:Location {id:'node_15', name:'Library', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (comp_lab:Location {id:'node_16', name:'Computer Lab', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (corridor_c:Location {id:'node_17', name:'Corridor Junction C', floor:0, venueId:'demo_venue', accessible:true, type:'junction'})
CREATE (admin:Location {id:'node_18', name:'Admin Office', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (faculty:Location {id:'node_19', name:'Faculty Room', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (corridor_d:Location {id:'node_20', name:'Corridor Junction D', floor:0, venueId:'demo_venue', accessible:true, type:'junction'})
CREATE (seminar:Location {id:'node_21', name:'Seminar Hall', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (accessibleRestroom:Location {id:'node_22', name:'Accessible Restroom', floor:0, venueId:'demo_venue', accessible:true, type:'toilet'})
CREATE (parking:Location {id:'node_23', name:'Parking Entrance', floor:0, venueId:'demo_venue', accessible:true, type:'entrance'})
CREATE (security:Location {id:'node_24', name:'Security Office', floor:0, venueId:'demo_venue', accessible:true, type:'room'})
CREATE (medical:Location {id:'node_25', name:'Medical Room', floor:0, venueId:'demo_venue', accessible:true, type:'room'})

// ==========================================
// Step 2.5: Generate and Store Vector Embeddings
// Since Neo4j handles Vector search via `db.index.vector.queryNodes`,
// we will create the vector index first.
// ==========================================
// Uncomment this in Neo4j exactly once:
// CALL db.index.vector.createNodeIndex('location_embeddings', 'Location', 'embedding', 768, 'cosine');

// Step 3: Create all edges (bidirectional)
CREATE (entrance)-[:CONNECTS_TO {distance_m:5.6, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:6}]->(lobby)
CREATE (lobby)-[:CONNECTS_TO {distance_m:5.6, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:6}]->(entrance)

CREATE (lobby)-[:CONNECTS_TO {distance_m:4.2, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:5}]->(reception)
CREATE (reception)-[:CONNECTS_TO {distance_m:4.2, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:5}]->(lobby)

CREATE (lobby)-[:CONNECTS_TO {distance_m:4.2, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:5}]->(lift)
CREATE (lift)-[:CONNECTS_TO {distance_m:4.2, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:5}]->(lobby)

CREATE (lobby)-[:CONNECTS_TO {distance_m:7.0, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:8}]->(corridor_a)
CREATE (corridor_a)-[:CONNECTS_TO {distance_m:7.0, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:8}]->(lobby)

CREATE (corridor_a)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(room_101)
CREATE (room_101)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(corridor_a)

CREATE (corridor_a)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(room_102)
CREATE (room_102)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(corridor_a)

CREATE (corridor_a)-[:CONNECTS_TO {distance_m:3.5, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_c)
CREATE (corridor_c)-[:CONNECTS_TO {distance_m:3.5, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_a)

CREATE (corridor_c)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(admin)
CREATE (admin)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(corridor_c)

CREATE (corridor_c)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(faculty)
CREATE (faculty)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(corridor_c)

CREATE (corridor_c)-[:CONNECTS_TO {distance_m:3.5, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_b)
CREATE (corridor_b)-[:CONNECTS_TO {distance_m:3.5, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_c)

CREATE (corridor_b)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(room_103)
CREATE (room_103)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(corridor_b)

CREATE (corridor_b)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(toilet_m)
CREATE (toilet_m)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(corridor_b)

CREATE (corridor_b)-[:CONNECTS_TO {distance_m:3.5, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_d)
CREATE (corridor_d)-[:CONNECTS_TO {distance_m:3.5, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_b)

CREATE (corridor_d)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(toilet_f)
CREATE (toilet_f)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(corridor_d)

CREATE (corridor_d)-[:CONNECTS_TO {distance_m:9.8, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:11}]->(medical)
CREATE (medical)-[:CONNECTS_TO {distance_m:9.8, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:11}]->(corridor_d)

CREATE (corridor_d)-[:CONNECTS_TO {distance_m:3.5, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(canteen)
CREATE (canteen)-[:CONNECTS_TO {distance_m:3.5, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(corridor_d)

CREATE (canteen)-[:CONNECTS_TO {distance_m:5.3, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:6}]->(exit_rear)
CREATE (exit_rear)-[:CONNECTS_TO {distance_m:5.3, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:6}]->(canteen)

CREATE (canteen)-[:CONNECTS_TO {distance_m:8.4, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:9}]->(seminar)
CREATE (seminar)-[:CONNECTS_TO {distance_m:8.4, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:9}]->(canteen)

CREATE (canteen)-[:CONNECTS_TO {distance_m:9.8, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:11}]->(comp_lab)
CREATE (comp_lab)-[:CONNECTS_TO {distance_m:9.8, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:11}]->(canteen)

CREATE (lobby)-[:CONNECTS_TO {distance_m:5.6, direction:'west', instruction:'Turn left', has_stairs:true, estimated_seconds:6}]->(stairs)
CREATE (stairs)-[:CONNECTS_TO {distance_m:5.6, direction:'east', instruction:'Turn right', has_stairs:true, estimated_seconds:6}]->(lobby)

CREATE (faculty)-[:CONNECTS_TO {distance_m:1.4, direction:'east', instruction:'Walk straight', has_stairs:false, estimated_seconds:2}]->(acc_toilet)
CREATE (acc_toilet)-[:CONNECTS_TO {distance_m:1.4, direction:'west', instruction:'Walk straight', has_stairs:false, estimated_seconds:2}]->(faculty)

CREATE (toilet_m)-[:CONNECTS_TO {distance_m:3.5, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(toilet_f)
CREATE (toilet_f)-[:CONNECTS_TO {distance_m:3.5, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(toilet_m)

CREATE (entrance)-[:CONNECTS_TO {distance_m:4.2, direction:'west', instruction:'Turn left', has_stairs:false, estimated_seconds:5}]->(security)
CREATE (security)-[:CONNECTS_TO {distance_m:4.2, direction:'east', instruction:'Turn right', has_stairs:false, estimated_seconds:5}]->(entrance)

CREATE (security)-[:CONNECTS_TO {distance_m:7.0, direction:'west', instruction:'Walk straight', has_stairs:false, estimated_seconds:8}]->(parking)
CREATE (parking)-[:CONNECTS_TO {distance_m:7.0, direction:'east', instruction:'Walk straight', has_stairs:false, estimated_seconds:8}]->(security)

CREATE (room_103)-[:CONNECTS_TO {distance_m:1.4, direction:'west', instruction:'Walk straight', has_stairs:false, estimated_seconds:2}]->(library)
CREATE (library)-[:CONNECTS_TO {distance_m:1.4, direction:'east', instruction:'Walk straight', has_stairs:false, estimated_seconds:2}]->(room_103)

CREATE (library)-[:CONNECTS_TO {distance_m:3.5, direction:'north', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(medical)
CREATE (medical)-[:CONNECTS_TO {distance_m:3.5, direction:'south', instruction:'Walk straight', has_stairs:false, estimated_seconds:4}]->(library)

// Step 4: Create semantic relationships for GraphRAG
CREATE (toilet_m)-[:NEAR]->(canteen)
CREATE (toilet_f)-[:NEAR]->(canteen)
CREATE (acc_toilet)-[:NEAR]->(faculty)
CREATE (lift)-[:ACCESSIBLE_FROM]->(corridor_a)

// Verify with:
// MATCH (n:Location) RETURN count(n) as nodeCount;
// MATCH ()-[r:CONNECTS_TO]->() RETURN count(r) as edgeCount;
// MATCH p=shortestPath((a:Location {name:'Main Entrance'})-[:CONNECTS_TO*]-(b:Location {name:'Accessible Restroom'})) RETURN p;
