INSERT INTO zcat_option_value.material_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (1, 'LEATHER', 'option_value.MATERIAL_TYPE.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (2, 'TEXTILE', 'option_value.MATERIAL_TYPE.TEXTILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (3, 'MISC', 'option_value.MATERIAL_TYPE.MISC.name', TRUE, 'bootstrap', 'bootstrap');


INSERT INTO zcat_option_value.sub_season (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (4, 'Q1', 'option_value.SUB_SEASON.Q1.name', TRUE, 'bootstrap', 'bootstrap'),
  (5, 'Q2', 'option_value.SUB_SEASON.Q2.name', TRUE, 'bootstrap', 'bootstrap'),
  (6, 'Q3', 'option_value.SUB_SEASON.Q3.name', TRUE, 'bootstrap', 'bootstrap'),
  (7, 'Q4', 'option_value.SUB_SEASON.Q4.name', TRUE, 'bootstrap', 'bootstrap'),
  (8, 'M01', 'option_value.SUB_SEASON.M01.name', TRUE, 'bootstrap', 'bootstrap'),
  (9, 'M02', 'option_value.SUB_SEASON.M02.name', TRUE, 'bootstrap', 'bootstrap'),
  (10, 'M03', 'option_value.SUB_SEASON.M03.name', TRUE, 'bootstrap', 'bootstrap'),
  (11, 'M04', 'option_value.SUB_SEASON.M04.name', TRUE, 'bootstrap', 'bootstrap'),
  (12, 'M05', 'option_value.SUB_SEASON.M05.name', TRUE, 'bootstrap', 'bootstrap'),
  (13, 'M06', 'option_value.SUB_SEASON.M06.name', TRUE, 'bootstrap', 'bootstrap'),
  (14, 'M07', 'option_value.SUB_SEASON.M07.name', TRUE, 'bootstrap', 'bootstrap'),
  (15, 'M08', 'option_value.SUB_SEASON.M08.name', TRUE, 'bootstrap', 'bootstrap'),
  (16, 'M09', 'option_value.SUB_SEASON.M09.name', TRUE, 'bootstrap', 'bootstrap'),
  (17, 'M10', 'option_value.SUB_SEASON.M10.name', TRUE, 'bootstrap', 'bootstrap'),
  (18, 'M11', 'option_value.SUB_SEASON.M11.name', TRUE, 'bootstrap', 'bootstrap'),
  (19, 'M12', 'option_value.SUB_SEASON.M12.name', TRUE, 'bootstrap', 'bootstrap'),
  (20, 'EARLY', 'option_value.SUB_SEASON.EARLY.name', TRUE, 'bootstrap', 'bootstrap'),
  (21, 'LATE', 'option_value.SUB_SEASON.LATE.name', TRUE, 'bootstrap', 'bootstrap'),
  (22, 'MAIN', 'option_value.SUB_SEASON.MAIN.name', TRUE, 'bootstrap', 'bootstrap'),
  (23, 'GENERAL', 'option_value.SUB_SEASON.GENERAL.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.availability (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (24, 'NORMAL', 'option_value.AVAILABILITY.NORMAL.name', TRUE, 'bootstrap', 'bootstrap'),
  (25, 'NOS', 'option_value.AVAILABILITY.NOS.name', TRUE, 'bootstrap', 'bootstrap'),
  (26, 'CARRY_OVER', 'option_value.AVAILABILITY.CARRY_OVER.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.tax_classification (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (27, 'FULL_TAX', 'option_value.TAX_CLASSIFICATION.FULL_TAX.name', TRUE, 'bootstrap', 'bootstrap'),
  (28, 'HALF_TAX', 'option_value.TAX_CLASSIFICATION.HALF_TAX.name', TRUE, 'bootstrap', 'bootstrap'),
  (29, 'NO_TAX', 'option_value.TAX_CLASSIFICATION.NO_TAX.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.bootleg_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (30, 'COVERING_ANKLE', 'option_value.BOOTLEG_TYPE.COVERING_ANKLE.name', TRUE, 'bootstrap', 'bootstrap'),
  (31, 'COVERING_CALF', 'option_value.BOOTLEG_TYPE.COVERING_CALF.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.shipping_placement (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (32, 'HANGING', 'option_value.SHIPPING_PLACEMENT.HANGING.name', TRUE, 'bootstrap', 'bootstrap'),
  (33, 'LYING', 'option_value.SHIPPING_PLACEMENT.LYING.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.fitting (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (34, 'SMALLER', 'option_value.FITTING.SMALLER.name', TRUE, 'bootstrap', 'bootstrap'),
  (35, 'LARGER', 'option_value.FITTING.LARGER.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.closure (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (36, 'ZIP', 'option_value.CLOSURE.ZIP.name', TRUE, 'bootstrap', 'bootstrap'),
  (37, 'BUTTON', 'option_value.CLOSURE.BUTTON.name', TRUE, 'bootstrap', 'bootstrap'),
  (38, 'HOOK_AND_EYE', 'option_value.CLOSURE.HOOK_AND_EYE.name', TRUE, 'bootstrap', 'bootstrap'),
  (39, 'TOGGLES', 'option_value.CLOSURE.TOGGLES.name', TRUE, 'bootstrap', 'bootstrap'),
  (40, 'BELT', 'option_value.CLOSURE.BELT.name', TRUE, 'bootstrap', 'bootstrap'),
  (41, 'DRAWSTRING', 'option_value.CLOSURE.DRAWSTRING.name', TRUE, 'bootstrap', 'bootstrap'),
  (42, 'ELASTIC', 'option_value.CLOSURE.ELASTIC.name', TRUE, 'bootstrap', 'bootstrap'),
  (43, 'COMBINATION', 'option_value.CLOSURE.COMBINATION.name', TRUE, 'bootstrap', 'bootstrap'),
  (44, 'LACES', 'option_value.CLOSURE.LACES.name', TRUE, 'bootstrap', 'bootstrap'),
  (45, 'BUCKLE', 'option_value.CLOSURE.BUCKLE.name', TRUE, 'bootstrap', 'bootstrap'),
  (46, 'HOOK_AND_LOOP', 'option_value.CLOSURE.HOOK_AND_LOOP.name', TRUE, 'bootstrap', 'bootstrap'),
  (47, 'NONE', 'option_value.CLOSURE.NONE.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.toe_cap (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (48, 'POINTED', 'option_value.TOE_CAP.POINTED.name', TRUE, 'bootstrap', 'bootstrap'),
  (49, 'ROUND', 'option_value.TOE_CAP.ROUND.name', TRUE, 'bootstrap', 'bootstrap'),
  (50, 'SQUARE', 'option_value.TOE_CAP.SQUARE.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.sleeve_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (51, 'SLEEVELESS', 'option_value.SLEEVE_TYPE.SLEEVELESS.name', TRUE, 'bootstrap', 'bootstrap'),
  (52, 'SHORT_SLEEVES', 'option_value.SLEEVE_TYPE.SHORT_SLEEVES.name', TRUE, 'bootstrap', 'bootstrap'),
  (53, 'THREE_QUARTER', 'option_value.SLEEVE_TYPE.THREE_QUARTER.name', TRUE, 'bootstrap', 'bootstrap'),
  (54, 'LONG_SLEEVES', 'option_value.SLEEVE_TYPE.LONG_SLEEVES.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.heel_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (55, 'WEDGE', 'option_value.HEEL_TYPE.WEDGE.name', TRUE, 'bootstrap', 'bootstrap'),
  (56, 'BLOCK_HEEL', 'option_value.HEEL_TYPE.BLOCK_HEEL.name', TRUE, 'bootstrap', 'bootstrap'),
  (57, 'STILETTO_LOW', 'option_value.HEEL_TYPE.STILETTO_LOW.name', TRUE, 'bootstrap', 'bootstrap'),
  (58, 'STILETTO_HIGH', 'option_value.HEEL_TYPE.STILETTO_HIGH.name', TRUE, 'bootstrap', 'bootstrap'),
  (59, 'CONE_HEEL', 'option_value.HEEL_TYPE.CONE_HEEL.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.leg_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (60, 'NORMAL', 'option_value.LEG_TYPE.NORMAL.name', TRUE, 'bootstrap', 'bootstrap'),
  (61, 'SHORT', 'option_value.LEG_TYPE.SHORT.name', TRUE, 'bootstrap', 'bootstrap'),
  (62, 'LONG', 'option_value.LEG_TYPE.LONG.name', TRUE, 'bootstrap', 'bootstrap'),
  (63, 'THREE_QUARTER', 'option_value.LEG_TYPE.THREE_QUARTER.name', TRUE, 'bootstrap', 'bootstrap'),
  (64, 'SEVEN_EIGHTH', 'option_value.LEG_TYPE.SEVEN_EIGHTH.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.neck_line (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (65, 'ROUND_NECK', 'option_value.NECK_LINE.ROUND_NECK.name', TRUE, 'bootstrap', 'bootstrap'),
  (66, 'V_NECK', 'option_value.NECK_LINE.V_NECK.name', TRUE, 'bootstrap', 'bootstrap'),
  (67, 'BUTTON_UP_WITH_COLLAR', 'option_value.NECK_LINE.BUTTON_UP_WITH_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (68, 'ZIP_COLLAR', 'option_value.NECK_LINE.ZIP_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (69, 'TURTLE_NECK', 'option_value.NECK_LINE.TURTLE_NECK.name', TRUE, 'bootstrap', 'bootstrap'),
  (70, 'HOODED', 'option_value.NECK_LINE.HOODED.name', TRUE, 'bootstrap', 'bootstrap'),
  (71, 'CUT_AWAY_COLLAR', 'option_value.NECK_LINE.CUT_AWAY_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (72, 'KENT_COLLAR', 'option_value.NECK_LINE.KENT_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (73, 'BUTTON_DOWN_COLLAR', 'option_value.NECK_LINE.BUTTON_DOWN_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (74, 'PETER_PAN_COLLAR', 'option_value.NECK_LINE.PETER_PAN_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (75, 'BOAT_NECK', 'option_value.NECK_LINE.BOAT_NECK.name', TRUE, 'bootstrap', 'bootstrap'),
  (76, 'WATERFALL', 'option_value.NECK_LINE.WATERFALL.name', TRUE, 'bootstrap', 'bootstrap'),
  (77, 'MANDARIN_COLLAR', 'option_value.NECK_LINE.MANDARIN_COLLAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (78, 'WRAP_EFFECT', 'option_value.NECK_LINE.WRAP_EFFECT.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.shoe_upper (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (79, 'LEATHER', 'option_value.SHOE_UPPER.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (80, 'TEXTILE', 'option_value.SHOE_UPPER.TEXTILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (81, 'IMITATION_LEATHER', 'option_value.SHOE_UPPER.IMITATION_LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (82, 'RUBBER', 'option_value.SHOE_UPPER.RUBBER.name', TRUE, 'bootstrap', 'bootstrap'),
  (83, 'COMBINATION', 'option_value.SHOE_UPPER.COMBINATION.name', TRUE, 'bootstrap', 'bootstrap'),
  (84, 'FUR', 'option_value.SHOE_UPPER.FUR.name', TRUE, 'bootstrap', 'bootstrap'),
  (85, 'SYNTHETICS', 'option_value.SHOE_UPPER.SYNTHETICS.name', TRUE, 'bootstrap', 'bootstrap'),
  (86, 'LEATHER_AND_LEATHER', 'option_value.SHOE_UPPER.LEATHER_AND_LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (87, 'LEATHER_AND_TEXTILE', 'option_value.SHOE_UPPER.LEATHER_AND_TEXTILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (88, 'LEATHER_AND_SYNTHETICS', 'option_value.SHOE_UPPER.LEATHER_AND_SYNTHETICS.name', TRUE, 'bootstrap', 'bootstrap'),
  (89, 'TEXTILE_AND_SYNTHETICS', 'option_value.SHOE_UPPER.TEXTILE_AND_SYNTHETICS.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.textile_membrane (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (90, 'NONE', 'option_value.TEXTILE_MEMBRANE.NONE.name', TRUE, 'bootstrap', 'bootstrap'),
  (91, 'GORETEX', 'option_value.TEXTILE_MEMBRANE.GORETEX.name', TRUE, 'bootstrap', 'bootstrap'),
  (92, 'SYMPATEX', 'option_value.TEXTILE_MEMBRANE.SYMPATEX.name', TRUE, 'bootstrap', 'bootstrap'),
  (93, 'OTHER', 'option_value.TEXTILE_MEMBRANE.OTHER.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.fit_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (94, 'NORMAL', 'option_value.FIT_TYPE.normal.name', TRUE, 'bootstrap', 'bootstrap'),
  (95, 'BOOTCUT', 'option_value.FIT_TYPE.bootcut.name', TRUE, 'bootstrap', 'bootstrap'),
  (96, 'STRAIGHT_LEG', 'option_value.FIT_TYPE.straight_leg.name', TRUE, 'bootstrap', 'bootstrap'),
  (97, 'SLIM_FIT', 'option_value.FIT_TYPE.slim_fit.name', TRUE, 'bootstrap', 'bootstrap'),
  (98, 'BOYFRIEND', 'option_value.FIT_TYPE.boyfriend.name', TRUE, 'bootstrap', 'bootstrap'),
  (99, 'TAPERED_FIT', 'option_value.FIT_TYPE.tapered_fit.name', TRUE, 'bootstrap', 'bootstrap'),
  (100, 'RELAXED_FIT', 'option_value.FIT_TYPE.relaxed_fit.name', TRUE, 'bootstrap', 'bootstrap'),
  (101, 'SKINNY_FIT', 'option_value.FIT_TYPE.skinny_fit.name', TRUE, 'bootstrap', 'bootstrap'),
  (102, 'JEGGINGS', 'option_value.FIT_TYPE.jeggings.name', TRUE, 'bootstrap', 'bootstrap'),
  (103, 'LOOSE_FIT', 'option_value.FIT_TYPE.loose_fit.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.sole_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (104, 'THUNIT', 'option_value.SOLE_TYPE.THUNIT.name', TRUE, 'bootstrap', 'bootstrap'),
  (105, 'RUBBER', 'option_value.SOLE_TYPE.RUBBER.name', TRUE, 'bootstrap', 'bootstrap'),
  (106, 'LEATHER', 'option_value.SOLE_TYPE.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (107, 'PLASTIC', 'option_value.SOLE_TYPE.PLASTIC.name', TRUE, 'bootstrap', 'bootstrap'),
  (108, 'PLASTIC_AND_LEATHER', 'option_value.SOLE_TYPE.PLASTIC_AND_LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (109, 'PLASTIC_AND_THUNIT', 'option_value.SOLE_TYPE.PLASTIC_AND_THUNIT.name', TRUE, 'bootstrap', 'bootstrap'),
  (110, 'PLASTIC_WITH_SHOCK_ABSORPTION', 'option_value.SOLE_TYPE.PLASTIC_WITH_SHOCK_ABSORPTION.name', TRUE, 'bootstrap', 'bootstrap'),
  (111, 'WOOD', 'option_value.SOLE_TYPE.WOOD.name', TRUE, 'bootstrap', 'bootstrap'),
  (112, 'CREPE_RUBBER', 'option_value.SOLE_TYPE.CREPE_RUBBER.name', TRUE, 'bootstrap', 'bootstrap'),
  (113, 'CORK', 'option_value.SOLE_TYPE.CORK.name', TRUE, 'bootstrap', 'bootstrap'),
  (114, 'RAFFIA', 'option_value.SOLE_TYPE.RAFFIA.name', TRUE, 'bootstrap', 'bootstrap'),
  (115, 'NATURAL_RUBBER', 'option_value.SOLE_TYPE.NATURAL_RUBBER.name', TRUE, 'bootstrap', 'bootstrap'),
  (116, 'STUDS', 'option_value.SOLE_TYPE.STUDS.name', TRUE, 'bootstrap', 'bootstrap'),
  (117, 'TEXTURED', 'option_value.SOLE_TYPE.TEXTURED.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.insole_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (118, 'LEATHER', 'option_value.INSOLE_TYPE.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (119, 'IMITATION_LEATHER', 'option_value.INSOLE_TYPE.IMITATION_LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (120, 'TEXTILE', 'option_value.INSOLE_TYPE.TEXTILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (121, 'FLEECE', 'option_value.INSOLE_TYPE.FLEECE.name', TRUE, 'bootstrap', 'bootstrap'),
  (122, 'WOVEN_STRAW', 'option_value.INSOLE_TYPE.WOVEN_STRAW.name', TRUE, 'bootstrap', 'bootstrap'),
  (123, 'WOOD', 'option_value.INSOLE_TYPE.WOOD.name', TRUE, 'bootstrap', 'bootstrap'),
  (124, 'JUTE', 'option_value.INSOLE_TYPE.JUTE.name', TRUE, 'bootstrap', 'bootstrap'),
  (125, 'CORK', 'option_value.INSOLE_TYPE.CORK.name', TRUE, 'bootstrap', 'bootstrap'),
  (126, 'LAMBSKIN', 'option_value.INSOLE_TYPE.LAMBSKIN.name', TRUE, 'bootstrap', 'bootstrap'),
  (127, 'WOOL', 'option_value.INSOLE_TYPE.WOOL.name', TRUE, 'bootstrap', 'bootstrap'),
  (128, 'LEATHER_AND_TEXTILE', 'option_value.INSOLE_TYPE.LEATHER_AND_TEXTILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (129, 'MERINO', 'option_value.INSOLE_TYPE.MERINO.name', TRUE, 'bootstrap', 'bootstrap'),
  (130, 'NYLON', 'option_value.INSOLE_TYPE.NYLON.name', TRUE, 'bootstrap', 'bootstrap'),
  (131, 'PURE_NEW_WOOL', 'option_value.INSOLE_TYPE.PURE_NEW_WOOL.name', TRUE, 'bootstrap', 'bootstrap'),
  (132, 'PLASTIC', 'option_value.INSOLE_TYPE.PLASTIC.name', TRUE, 'bootstrap', 'bootstrap'),
  (133, 'MICROFIBRE', 'option_value.INSOLE_TYPE.MICROFIBRE.name', TRUE, 'bootstrap', 'bootstrap'),
  (134, 'FAUX_FUR', 'option_value.INSOLE_TYPE.FAUX_FUR.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.textile_upper (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (135, 'PURE_NEW_WOOL', 'option_value.TEXTILE_UPPER.PURE_NEW_WOOL.name', TRUE, 'bootstrap', 'bootstrap'),
  (136, 'LAMB_WOOL', 'option_value.TEXTILE_UPPER.LAMB_WOOL.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.pattern (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (137, 'PLAIN', 'option_value.PATTERN.PLAIN.name', TRUE, 'bootstrap', 'bootstrap'),
  (138, 'CHECKED', 'option_value.PATTERN.CHECKED.name', TRUE, 'bootstrap', 'bootstrap'),
  (139, 'STRIPES', 'option_value.PATTERN.STRIPES.name', TRUE, 'bootstrap', 'bootstrap'),
  (140, 'ANIMAL_PRINT', 'option_value.PATTERN.ANIMAL_PRINT.name', TRUE, 'bootstrap', 'bootstrap'),
  (141, 'FLORAL', 'option_value.PATTERN.FLORAL.name', TRUE, 'bootstrap', 'bootstrap'),
  (142, 'OTHER', 'option_value.PATTERN.OTHER.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.lining_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (143, 'NONE', 'option_value.LINING_TYPE.NONE.name', TRUE, 'bootstrap', 'bootstrap'),
  (144, 'COLD', 'option_value.LINING_TYPE.COLD.name', TRUE, 'bootstrap', 'bootstrap'),
  (145, 'WARM', 'option_value.LINING_TYPE.WARM.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.shoe_lining_material (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (146, 'LEATHER', 'option_value.SHOE_LINING_MATERIAL.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (147, 'TEXTILE', 'option_value.SHOE_LINING_MATERIAL.TEXTILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (148, 'IMITATION_LEATHER', 'option_value.SHOE_LINING_MATERIAL.IMITATION_LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (149, 'SYNTHETIC', 'option_value.SHOE_LINING_MATERIAL.SYNTHETIC.name', TRUE, 'bootstrap', 'bootstrap'),
  (150, 'WOOL', 'option_value.SHOE_LINING_MATERIAL.WOOL.name', TRUE, 'bootstrap', 'bootstrap'),
  (151, 'LAMB_SKIN', 'option_value.SHOE_LINING_MATERIAL.LAMB_SKIN.name', TRUE, 'bootstrap', 'bootstrap'),
  (152, 'SYNTHETIC_FUR', 'option_value.SHOE_LINING_MATERIAL.SYNTHETIC_FUR.name', TRUE, 'bootstrap', 'bootstrap'),
  (153, 'NO_LINING', 'option_value.SHOE_LINING_MATERIAL.NO_LINING.name', TRUE, 'bootstrap', 'bootstrap'),
  (154, 'MICROFIBRE', 'option_value.SHOE_LINING_MATERIAL.MICROFIBRE.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.textile_lining_material (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (155, 'VISCOSE_100_PERCENT', 'option_value.TEXTILE_LINING_MATERIAL.VISCOSE_100_PERCENT.name', TRUE, 'bootstrap', 'bootstrap'),
  (156, 'POLYESTER_100_PERCENT', 'option_value.TEXTILE_LINING_MATERIAL.POLYESTER_100_PERCENT.name', TRUE, 'bootstrap', 'bootstrap'),
  (157, 'COTTON_100_PERCENT', 'option_value.TEXTILE_LINING_MATERIAL.COTTON_100_PERCENT.name', TRUE, 'bootstrap', 'bootstrap'),
  (158, 'PES_ELA_BLEND', 'option_value.TEXTILE_LINING_MATERIAL.PES_ELA_BLEND.name', TRUE, 'bootstrap', 'bootstrap'),
  (159, 'CO_ELA_BLEND', 'option_value.TEXTILE_LINING_MATERIAL.CO_ELA_BLEND.name', TRUE, 'bootstrap', 'bootstrap'),
  (160, 'PES_CV_BLEND', 'option_value.TEXTILE_LINING_MATERIAL.PES_CV_BLEND.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.leather_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (161, 'LEATHER', 'option_value.LEATHER_TYPE.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (162, 'SUEDE', 'option_value.LEATHER_TYPE.SUEDE.name', TRUE, 'bootstrap', 'bootstrap'),
  (163, 'NUBUCK', 'option_value.LEATHER_TYPE.NUBUCK.name', TRUE, 'bootstrap', 'bootstrap'),
  (164, 'NAPPA', 'option_value.LEATHER_TYPE.NAPPA.name', TRUE, 'bootstrap', 'bootstrap'),
  (165, 'PATENT_LEATHER', 'option_value.LEATHER_TYPE.PATENT_LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (166, 'METALLIC_LEATHER', 'option_value.LEATHER_TYPE.METALLIC_LEATHER.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.sport_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (167, 'OUTDOOR', 'option_value.SPORT_TYPE.OUTDOOR.name', TRUE, 'bootstrap', 'bootstrap'),
  (168, 'RUNNING', 'option_value.SPORT_TYPE.RUNNING.name', TRUE, 'bootstrap', 'bootstrap'),
  (169, 'SOCCER', 'option_value.SPORT_TYPE.SOCCER.name', TRUE, 'bootstrap', 'bootstrap'),
  (170, 'BEACH', 'option_value.SPORT_TYPE.BEACH.name', TRUE, 'bootstrap', 'bootstrap'),
  (171, 'TRAINING', 'option_value.SPORT_TYPE.TRAINING.name', TRUE, 'bootstrap', 'bootstrap'),
  (172, 'MARTIAL_ARTS', 'option_value.SPORT_TYPE.MARTIAL_ARTS.name', TRUE, 'bootstrap', 'bootstrap'),
  (173, 'GOLF', 'option_value.SPORT_TYPE.GOLF.name', TRUE, 'bootstrap', 'bootstrap'),
  (174, 'BASKETBALL', 'option_value.SPORT_TYPE.BASKETBALL.name', TRUE, 'bootstrap', 'bootstrap'),
  (175, 'VOLLEYBALL', 'option_value.SPORT_TYPE.VOLLEYBALL.name', TRUE, 'bootstrap', 'bootstrap'),
  (176, 'HANDBALL', 'option_value.SPORT_TYPE.HANDBALL.name', TRUE, 'bootstrap', 'bootstrap'),
  (177, 'TENNIS', 'option_value.SPORT_TYPE.TENNIS.name', TRUE, 'bootstrap', 'bootstrap'),
  (178, 'BADMINTON', 'option_value.SPORT_TYPE.BADMINTON.name', TRUE, 'bootstrap', 'bootstrap'),
  (179, 'SQUASH', 'option_value.SPORT_TYPE.SQUASH.name', TRUE, 'bootstrap', 'bootstrap'),
  (180, 'TABLE_TENNIS', 'option_value.SPORT_TYPE.TABLE_TENNIS.name', TRUE, 'bootstrap', 'bootstrap'),
  (181, 'SKI_AND_SNOW', 'option_value.SPORT_TYPE.SKI_AND_SNOW.name', TRUE, 'bootstrap', 'bootstrap'),
  (182, 'CYCLING', 'option_value.SPORT_TYPE.CYCLING.name', TRUE, 'bootstrap', 'bootstrap'),
  (183, 'EQUITATION', 'option_value.SPORT_TYPE.EQUITATION.name', TRUE, 'bootstrap', 'bootstrap'),
  (184, 'HUNTING', 'option_value.SPORT_TYPE.HUNTING.name', TRUE, 'bootstrap', 'bootstrap'),
  (185, 'FISHING', 'option_value.SPORT_TYPE.FISHING.name', TRUE, 'bootstrap', 'bootstrap'),
  (186, 'SAILING', 'option_value.SPORT_TYPE.SAILING.name', TRUE, 'bootstrap', 'bootstrap'),
  (187, 'SKATING', 'option_value.SPORT_TYPE.SKATING.name', TRUE, 'bootstrap', 'bootstrap'),
  (188, 'HOCKEY', 'option_value.SPORT_TYPE.HOCKEY.name', TRUE, 'bootstrap', 'bootstrap'),
  (189, 'RUGBY', 'option_value.SPORT_TYPE.RUGBY.name', TRUE, 'bootstrap', 'bootstrap'),
  (190, 'US_SPORTS', 'option_value.SPORT_TYPE.US_SPORTS.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.sub_sport_type (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (191, 'URBAN_OUTDOORS', 'option_value.SUB_SPORT_TYPE.URBAN_OUTDOORS.name', TRUE, 'bootstrap', 'bootstrap'),
  (192, 'WALKING', 'option_value.SUB_SPORT_TYPE.WALKING.name', TRUE, 'bootstrap', 'bootstrap'),
  (193, 'HIKING', 'option_value.SUB_SPORT_TYPE.HIKING.name', TRUE, 'bootstrap', 'bootstrap'),
  (194, 'TREKKING', 'option_value.SUB_SPORT_TYPE.TREKKING.name', TRUE, 'bootstrap', 'bootstrap'),
  (195, 'MOUNTAINEERING', 'option_value.SUB_SPORT_TYPE.MOUNTAINEERING.name', TRUE, 'bootstrap', 'bootstrap'),
  (196, 'CLIMBING', 'option_value.SUB_SPORT_TYPE.CLIMBING.name', TRUE, 'bootstrap', 'bootstrap'),
  (197, 'CAMPING', 'option_value.SUB_SPORT_TYPE.CAMPING.name', TRUE, 'bootstrap', 'bootstrap'),
  (198, 'RUNNING', 'option_value.SUB_SPORT_TYPE.RUNNING.name', TRUE, 'bootstrap', 'bootstrap'),
  (199, 'NATURAL_RUNNING', 'option_value.SUB_SPORT_TYPE.NATURAL_RUNNING.name', TRUE, 'bootstrap', 'bootstrap'),
  (200, 'ATHLETICS', 'option_value.SUB_SPORT_TYPE.ATHLETICS.name', TRUE, 'bootstrap', 'bootstrap'),
  (201, 'TRIATHLON', 'option_value.SUB_SPORT_TYPE.TRIATHLON.name', TRUE, 'bootstrap', 'bootstrap'),
  (202, 'SURFING', 'option_value.SUB_SPORT_TYPE.SURFING.name', TRUE, 'bootstrap', 'bootstrap'),
  (203, 'SWIMWEAR', 'option_value.SUB_SPORT_TYPE.SWIMWEAR.name', TRUE, 'bootstrap', 'bootstrap'),
  (204, 'BEACH_VOLLEYBALL', 'option_value.SUB_SPORT_TYPE.BEACH_VOLLEYBALL.name', TRUE, 'bootstrap', 'bootstrap'),
  (205, 'SWIMMING', 'option_value.SUB_SPORT_TYPE.SWIMMING.name', TRUE, 'bootstrap', 'bootstrap'),
  (206, 'KITE_SURFING', 'option_value.SUB_SPORT_TYPE.KITE_SURFING.name', TRUE, 'bootstrap', 'bootstrap'),
  (207, 'DIVING', 'option_value.SUB_SPORT_TYPE.DIVING.name', TRUE, 'bootstrap', 'bootstrap'),
  (208, 'WAKEBOARDING', 'option_value.SUB_SPORT_TYPE.WAKEBOARDING.name', TRUE, 'bootstrap', 'bootstrap'),
  (209, 'FITNESS', 'option_value.SUB_SPORT_TYPE.FITNESS.name', TRUE, 'bootstrap', 'bootstrap'),
  (210, 'YOGA_AND_PILATES', 'option_value.SUB_SPORT_TYPE.YOGA_AND_PILATES.name', TRUE, 'bootstrap', 'bootstrap'),
  (211, 'BODY_BUILDING', 'option_value.SUB_SPORT_TYPE.BODY_BUILDING.name', TRUE, 'bootstrap', 'bootstrap'),
  (212, 'BOXING', 'option_value.SUB_SPORT_TYPE.BOXING.name', TRUE, 'bootstrap', 'bootstrap'),
  (213, 'TAEKWONDO', 'option_value.SUB_SPORT_TYPE.TAEKWONDO.name', TRUE, 'bootstrap', 'bootstrap'),
  (214, 'JUDO', 'option_value.SUB_SPORT_TYPE.JUDO.name', TRUE, 'bootstrap', 'bootstrap'),
  (215, 'ALPINE_SKIING', 'option_value.SUB_SPORT_TYPE.ALPINE_SKIING.name', TRUE, 'bootstrap', 'bootstrap'),
  (216, 'SNOWBOARDING', 'option_value.SUB_SPORT_TYPE.SNOWBOARDING.name', TRUE, 'bootstrap', 'bootstrap'),
  (217, 'CROSS_COUNTRY_SKIING', 'option_value.SUB_SPORT_TYPE.CROSS_COUNTRY_SKIING.name', TRUE, 'bootstrap', 'bootstrap'),
  (218, 'BACK_COUNTRY_SKIING', 'option_value.SUB_SPORT_TYPE.BACK_COUNTRY_SKIING.name', TRUE, 'bootstrap', 'bootstrap'),
  (219, 'CYCLING', 'option_value.SUB_SPORT_TYPE.CYCLING.name', TRUE, 'bootstrap', 'bootstrap'),
  (220, 'MOUNTAIN_BIKING', 'option_value.SUB_SPORT_TYPE.MOUNTAIN_BIKING.name', TRUE, 'bootstrap', 'bootstrap'),
  (221, 'INLINE_SKATING', 'option_value.SUB_SPORT_TYPE.INLINE_SKATING.name', TRUE, 'bootstrap', 'bootstrap'),
  (222, 'SKATEBOARDING', 'option_value.SUB_SPORT_TYPE.SKATEBOARDING.name', TRUE, 'bootstrap', 'bootstrap'),
  (223, 'ICE_SKATING', 'option_value.SUB_SPORT_TYPE.ICE_SKATING.name', TRUE, 'bootstrap', 'bootstrap'),
  (224, 'ICE_HOCKEY', 'option_value.SUB_SPORT_TYPE.ICE_HOCKEY.name', TRUE, 'bootstrap', 'bootstrap'),
  (225, 'FOOTBALL', 'option_value.SUB_SPORT_TYPE.FOOTBALL.name', TRUE, 'bootstrap', 'bootstrap'),
  (226, 'BASEBALL', 'option_value.SUB_SPORT_TYPE.BASEBALL.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.target_group_age (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (227, 'BIG_CHILD', 'option_value.TARGET_GROUP_AGE.BIG_CHILD.name', TRUE, 'bootstrap', 'bootstrap'),
  (228, 'SMALL_CHILD', 'option_value.TARGET_GROUP_AGE.SMALL_CHILD.name', TRUE, 'bootstrap', 'bootstrap'),
  (229, 'BABY', 'option_value.TARGET_GROUP_AGE.BABY.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.production_material (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (230, 'CIRCULAR_KNIT', 'option_value.PRODUCTION_MATERIAL.CIRCULAR_KNIT.name', TRUE, 'bootstrap', 'bootstrap'),
  (231, 'WOVEN', 'option_value.PRODUCTION_MATERIAL.WOVEN.name', TRUE, 'bootstrap', 'bootstrap'),
  (232, 'FLAT_KNIT', 'option_value.PRODUCTION_MATERIAL.FLAT_KNIT.name', TRUE, 'bootstrap', 'bootstrap'),
  (233, 'LEATHER', 'option_value.PRODUCTION_MATERIAL.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (234, 'POLYURETHANE', 'option_value.PRODUCTION_MATERIAL.POLYURETHANE.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.type_q (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (235, 'CIRCULAR_KNIT', 'option_value.TYPE_Q.CIRCULAR_KNIT.name', TRUE, 'bootstrap', 'bootstrap'),
  (236, 'WOVEN', 'option_value.TYPE_Q.WOVEN.name', TRUE, 'bootstrap', 'bootstrap'),
  (237, 'FLAT_KNIT', 'option_value.TYPE_Q.FLAT_KNIT.name', TRUE, 'bootstrap', 'bootstrap'),
  (238, 'LEATHER', 'option_value.TYPE_Q.LEATHER.name', TRUE, 'bootstrap', 'bootstrap'),
  (239, 'POLYURETHANE', 'option_value.TYPE_Q.POLYURETHANE.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.material_detail (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (240, 'SINGLE_JERSEY', 'option_value.MATERIAL_DETAIL.SINGLE_JERSEY.name', TRUE, 'bootstrap', 'bootstrap'),
  (241, 'INTERLOCK', 'option_value.MATERIAL_DETAIL.INTERLOCK.name', TRUE, 'bootstrap', 'bootstrap'),
  (242, 'FELPA', 'option_value.MATERIAL_DETAIL.FELPA.name', TRUE, 'bootstrap', 'bootstrap'),
  (243, 'FLEECE', 'option_value.MATERIAL_DETAIL.FLEECE.name', TRUE, 'bootstrap', 'bootstrap'),
  (244, 'POINTELLE', 'option_value.MATERIAL_DETAIL.POINTELLE.name', TRUE, 'bootstrap', 'bootstrap'),
  (245, 'ONE_X_ONE_RIB', 'option_value.MATERIAL_DETAIL.ONE_X_ONE_RIB.name', TRUE, 'bootstrap', 'bootstrap'),
  (246, 'TWO_X_TWO_RIB', 'option_value.MATERIAL_DETAIL.TWO_X_TWO_RIB.name', TRUE, 'bootstrap', 'bootstrap'),
  (247, 'TWILL', 'option_value.MATERIAL_DETAIL.TWILL.name', TRUE, 'bootstrap', 'bootstrap'),
  (248, 'CANVAS', 'option_value.MATERIAL_DETAIL.CANVAS.name', TRUE, 'bootstrap', 'bootstrap'),
  (249, 'BABY_CANVAS', 'option_value.MATERIAL_DETAIL.BABY_CANVAS.name', TRUE, 'bootstrap', 'bootstrap'),
  (250, 'CORD', 'option_value.MATERIAL_DETAIL.CORD.name', TRUE, 'bootstrap', 'bootstrap'),
  (251, 'HERRINGBONE', 'option_value.MATERIAL_DETAIL.HERRINGBONE.name', TRUE, 'bootstrap', 'bootstrap'),
  (252, 'LACE', 'option_value.MATERIAL_DETAIL.LACE.name', TRUE, 'bootstrap', 'bootstrap'),
  (253, 'KNIT_CROCHET', 'option_value.MATERIAL_DETAIL.KNIT_CROCHET.name', TRUE, 'bootstrap', 'bootstrap'),
  (254, 'JACQUARD', 'option_value.MATERIAL_DETAIL.JACQUARD.name', TRUE, 'bootstrap', 'bootstrap'),
  (255, 'POPLIN', 'option_value.MATERIAL_DETAIL.POPLIN.name', TRUE, 'bootstrap', 'bootstrap'),
  (256, 'VOILE', 'option_value.MATERIAL_DETAIL.VOILE.name', TRUE, 'bootstrap', 'bootstrap'),
  (257, 'SATIN', 'option_value.MATERIAL_DETAIL.SATIN.name', TRUE, 'bootstrap', 'bootstrap'),
  (258, 'SLUB', 'option_value.MATERIAL_DETAIL.SLUB.name', TRUE, 'bootstrap', 'bootstrap'),
  (259, 'BOUCLEE', 'option_value.MATERIAL_DETAIL.BOUCLEE.name', TRUE, 'bootstrap', 'bootstrap'),
  (260, 'NAP_YARN', 'option_value.MATERIAL_DETAIL.NAP_YARN.name', TRUE, 'bootstrap', 'bootstrap'),
  (261, 'DEVOREE', 'option_value.MATERIAL_DETAIL.DEVOREE.name', TRUE, 'bootstrap', 'bootstrap'),
  (262, 'CHECKED', 'option_value.MATERIAL_DETAIL.CHECKED.name', TRUE, 'bootstrap', 'bootstrap'),
  (263, 'WOOL', 'option_value.MATERIAL_DETAIL.WOOL.name', TRUE, 'bootstrap', 'bootstrap');

INSERT INTO zcat_option_value.trend (ov_id, ov_code, ov_name_message_key, ov_is_active, ov_created_by, ov_last_modified_by)
  VALUES
  (264, 'BATIK', 'option_value.TREND.BATIK.name', TRUE, 'bootstrap', 'bootstrap'),
  (265, 'BEACH_DRESSES', 'option_value.TREND.BEACH_DRESSES.name', TRUE, 'bootstrap', 'bootstrap'),
  (266, 'BRIGHTS', 'option_value.TREND.BRIGHTS.name', TRUE, 'bootstrap', 'bootstrap'),
  (267, 'CAMOUFLAGE', 'option_value.TREND.CAMOUFLAGE.name', TRUE, 'bootstrap', 'bootstrap'),
  (268, 'COLOUR_BLOCKING', 'option_value.TREND.COLOUR_BLOCKING.name', TRUE, 'bootstrap', 'bootstrap'),
  (269, 'COLOURED_DENIM_AND_CHINOS', 'option_value.TREND.COLOURED_DENIM_AND_CHINOS.name', TRUE, 'bootstrap', 'bootstrap'),
  (270, 'CREAM', 'option_value.TREND.CREAM.name', FALSE, 'bootstrap', 'bootstrap'),
  (271, 'DENIM_CLEAN_STYLES', 'option_value.TREND.DENIM_CLEAN_STYLES.name', TRUE, 'bootstrap', 'bootstrap'),
  (272, 'ESPADRILLES', 'option_value.TREND.ESPADRILLES.name', TRUE, 'bootstrap', 'bootstrap'),
  (273, 'FAWN', 'option_value.TREND.FAWN.name', FALSE, 'bootstrap', 'bootstrap'),
  (274, 'FIFTIES', 'option_value.TREND.FIFTIES.name', TRUE, 'bootstrap', 'bootstrap'),
  (275, 'FLORALS', 'option_value.TREND.FLORALS.name', TRUE, 'bootstrap', 'bootstrap'),
  (276, 'GREENS', 'option_value.TREND.GREENS.name', TRUE, 'bootstrap', 'bootstrap'),
  (277, 'GRUNGE_DE_LUXE', 'option_value.TREND.GRUNGE_DE_LUXE.name', TRUE, 'bootstrap', 'bootstrap'),
  (278, 'MATERIAL_BLOCKING', 'option_value.TREND.MATERIAL_BLOCKING.name', TRUE, 'bootstrap', 'bootstrap'),
  (279, 'MOUNTAINEERING', 'option_value.TREND.MOUNTAINEERING.name', TRUE, 'bootstrap', 'bootstrap'),
  (280, 'NAUTICAL', 'option_value.TREND.NAUTICAL.name', TRUE, 'bootstrap', 'bootstrap'),
  (281, 'NUDE', 'option_value.TREND.NUDE.name', FALSE, 'bootstrap', 'bootstrap'),
  (282, 'OFF_WHITE', 'option_value.TREND.OFF_WHITE.name', TRUE, 'bootstrap', 'bootstrap'),
  (283, 'ON_SAFARI', 'option_value.TREND.ON_SAFARI.name', TRUE, 'bootstrap', 'bootstrap'),
  (284, 'ORANGES_AND_REDS', 'option_value.TREND.ORANGES_AND_REDS.name', TRUE, 'bootstrap', 'bootstrap'),
  (285, 'PAISLEY', 'option_value.TREND.PAISLEY.name', TRUE, 'bootstrap', 'bootstrap'),
  (286, 'PASTELS', 'option_value.TREND.PASTELS.name', TRUE, 'bootstrap', 'bootstrap'),
  (287, 'PATENT', 'option_value.TREND.PATENT.name', TRUE, 'bootstrap', 'bootstrap'),
  (288, 'PATENT_PLATFORMS', 'option_value.TREND.PATENT_PLATFORMS.name', FALSE, 'bootstrap', 'bootstrap'),
  (289, 'PLATFORMS', 'option_value.TREND.PLATFORMS.name', TRUE, 'bootstrap', 'bootstrap'),
  (290, 'POLKA_DOTS', 'option_value.TREND.POLKA_DOTS.name', TRUE, 'bootstrap', 'bootstrap'),
  (291, 'PREPPY_CHIC', 'option_value.TREND.PREPPY_CHIC.name', TRUE, 'bootstrap', 'bootstrap'),
  (292, 'PRINTED_SHIRTS', 'option_value.TREND.PRINTED_SHIRTS.name', TRUE, 'bootstrap', 'bootstrap'),
  (293, 'PRINTED_SHORTS', 'option_value.TREND.PRINTED_SHORTS.name', TRUE, 'bootstrap', 'bootstrap'),
  (294, 'ROMANTIC_BOHO', 'option_value.TREND.ROMANTIC_BOHO.name', TRUE, 'bootstrap', 'bootstrap'),
  (295, 'SEVENTIES_TRIBAL', 'option_value.TREND.SEVENTIES_TRIBAL.name', TRUE, 'bootstrap', 'bootstrap'),
  (296, 'SHORTS_AND_HOT_PANTS', 'option_value.TREND.SHORTS_AND_HOT_PANTS.name', TRUE, 'bootstrap', 'bootstrap'),
  (297, 'SIXTIES', 'option_value.TREND.SIXTIES.name', TRUE, 'bootstrap', 'bootstrap'),
  (298, 'STRIPES', 'option_value.TREND.STRIPES.name', TRUE, 'bootstrap', 'bootstrap'),
  (299, 'SUMMER_BROWNS', 'option_value.TREND.SUMMER_BROWNS.name', TRUE, 'bootstrap', 'bootstrap'),
  (300, 'VINTAGE_AND_HERITAGE', 'option_value.TREND.VINTAGE_AND_HERITAGE.name', TRUE, 'bootstrap', 'bootstrap'),
  (301, 'WHITE', 'option_value.TREND.WHITE.name', TRUE, 'bootstrap', 'bootstrap'),
  (302, 'YELLOWS', 'option_value.TREND.YELLOWS.name', TRUE, 'bootstrap', 'bootstrap'),
  (303, 'VIBRANTCOLORS', 'option_value.TREND.VIBRANTCOLORS.name', FALSE, 'bootstrap', 'bootstrap'),
  (304, 'CHALKPASTEL', 'option_value.TREND.CHALKPASTEL.name', FALSE, 'bootstrap', 'bootstrap'),
  (305, 'CLEARVISION', 'option_value.TREND.CLEARVISION.name', FALSE, 'bootstrap', 'bootstrap'),
  (306, 'COLORBALANCE', 'option_value.TREND.COLORBALANCE.name', FALSE, 'bootstrap', 'bootstrap'),
  (307, 'FETISHFANTASY', 'option_value.TREND.FETISHFANTASY.name', FALSE, 'bootstrap', 'bootstrap'),
  (308, 'FRENCHPLEASURE', 'option_value.TREND.FRENCHPLEASURE.name', FALSE, 'bootstrap', 'bootstrap'),
  (309, 'NEWCLASSICS', 'option_value.TREND.NEWCLASSICS.name', FALSE, 'bootstrap', 'bootstrap'),
  (310, 'RETROSLIFEST', 'option_value.TREND.RETROSLIFEST.name', FALSE, 'bootstrap', 'bootstrap'),
  (311, 'WILDCRAFT', 'option_value.TREND.WILDCRAFT.name', FALSE, 'bootstrap', 'bootstrap'),
  (312, 'TRIBAL', 'option_value.TREND.TRIBAL.name', FALSE, 'bootstrap', 'bootstrap'),
  (313, 'PASTELLPOWER', 'option_value.TREND.PASTELLPOWER.name', FALSE, 'bootstrap', 'bootstrap'),
  (314, 'METALLICS', 'option_value.TREND.METALLICS.name', FALSE, 'bootstrap', 'bootstrap'),
  (315, 'NEWSPORTIVE', 'option_value.TREND.NEWSPORTIVE.name', FALSE, 'bootstrap', 'bootstrap'),
  (316, 'RETROROMANCE', 'option_value.TREND.RETROROMANCE.name', FALSE, 'bootstrap', 'bootstrap'),
  (317, 'PRINTSALLOVER', 'option_value.TREND.PRINTSALLOVER.name', FALSE, 'bootstrap', 'bootstrap'),
  (318, 'NEWCUT', 'option_value.TREND.NEWCUT.name', FALSE, 'bootstrap', 'bootstrap'),
  (319, 'COLORBLOCKINGCAS', 'option_value.TREND.COLORBLOCKINGCAS.name', FALSE, 'bootstrap', 'bootstrap'),
  (320, 'DENIM', 'option_value.TREND.DENIM.name', FALSE, 'bootstrap', 'bootstrap'),
  (321, 'FEMININE_SILHOUETTE', 'option_value.TREND.FEMININE_SILHOUETTE.name', FALSE, 'bootstrap', 'bootstrap');