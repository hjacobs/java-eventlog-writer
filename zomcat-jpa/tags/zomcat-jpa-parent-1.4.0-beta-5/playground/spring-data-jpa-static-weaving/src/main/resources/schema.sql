-- WUERDE SONST GENERIERT WERDEN, IST NUR AUS DEM LOG KOPIERT
CREATE TABLE purchase_order_position (pop_id INTEGER NOT NULL, pop_product_number VARCHAR(255), pop_quantity INTEGER, PRIMARY KEY (pop_id));
CREATE TABLE purchase_order (po_id INTEGER NOT NULL, po_business_key VARCHAR(255), PRIMARY KEY (po_id));
CREATE TABLE purchase_order_purchase_order_position (PurchaseOrder_ID INTEGER NOT NULL, positions_ID INTEGER NOT NULL, PRIMARY KEY (PurchaseOrder_ID, positions_ID));
ALTER TABLE purchase_order_purchase_order_position ADD CONSTRAINT purchaseorderpurchaseorderpositionPurchaseOrder_ID FOREIGN KEY (PurchaseOrder_ID) REFERENCES purchase_order (po_id);
ALTER TABLE purchase_order_purchase_order_position ADD CONSTRAINT purchase_order_purchase_order_positionpositions_ID FOREIGN KEY (positions_ID) REFERENCES purchase_order_position (pop_id);
CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT NUMERIC(38), PRIMARY KEY (SEQ_NAME));
SELECT * FROM SEQUENCE WHERE SEQ_NAME = SEQ_GEN;
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values (SEQ_GEN, 0);
