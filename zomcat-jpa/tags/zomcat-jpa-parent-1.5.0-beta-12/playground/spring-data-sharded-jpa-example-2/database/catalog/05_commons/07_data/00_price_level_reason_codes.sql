INSERT INTO zcat_commons.plr_price_change_method(plrpcm_value, plrpcm_description)
     VALUES ('1', 'Performance'),
            ('2', 'Marketing'),
            ('3', 'Assortment'),
            ('4', 'Competition');

INSERT INTO zcat_commons.plr_price_change_information(plrpci_value, plrpci_description)
     VALUES ('000', 'None'),
            ('011', 'Initial Discount'),
            ('012', 'Further Discount'),
            ('013', 'Sale'),
            ('021', 'Category Management'),
            ('022', 'Newsletter'),
            ('023', 'Print'),
            ('024', 'Campaigns'),
            ('025', 'Others'),
            ('031', 'Testing'),
            ('041', 'Brandshop'),
            ('042', 'Amazon'),
            ('043', 'Asos'),
            ('044', 'La Redoute'),
            ('045', 'Nelly'),
            ('046', 'Otto.de'),
            ('047', 'Sarenza'),
            ('048', '3 Suisses'),
            ('049', 'Yoox'),
            ('050', 'Spartoo'),
            ('051', 'Next');

INSERT INTO zcat_commons.plr_article_classification(plrac_value, plrac_description)
     VALUES ('0', 'None'),
            ('1', 'Risk Pool'),
            ('2', 'Key Value Item');