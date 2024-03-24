-- 수정 날짜: 2024-03-14
truncate table member;
truncate table team;
truncate table study;
truncate table curriculum_item;
truncate table participant_curriculum_item;
truncate table crop;
truncate table member_team;
truncate table participant;
truncate table garden;
truncate table document;
truncate table file;
truncate table attendance;

INSERT INTO document (name, description, group_type, group_id, is_deleted, access_type, type, uploader_id, created_at, updated_at)
VALUES
    ('Document from today', 'Description for Document 1', 'TEAM', 1, false, 'ALL', 'URL', 1, CURDATE(), NOW()),
    ('Document from other team', 'Description for Document 1', 'TEAM', 2, false, 'ALL', 'URL', 1, CURDATE(), NOW()),
    ('Document from monday', 'Description for Document 1', 'TEAM', 1, false, 'ALL', 'URL', 1, ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2), NOW()),
    ('Document from last week', 'Description for Document 1', 'TEAM', 1, false, 'ALL', 'URL', 1, ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())-5), NOW());

INSERT INTO study (name, description, start_date, end_date, status, is_deleted, team_id, crop_id, created_at, updated_at)
VALUES
    ('study from team 1', 'Description for study 1',  ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2), CURDATE(), "IN_PROGRESS", false, 1, 1, NOW(),NOW()),
    ('study from team 2', 'Description for study 2',  ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2), CURDATE(), "IN_PROGRESS", false, 2, 1, NOW(),NOW());

INSERT INTO curriculum_item (name, item_order, is_deleted, study_id, created_at, updated_at)
VALUES
    ('curriculum item from team 1', 1, false, 1, ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2), NOW()),
    ('curriculum item from team 2', 1, false, 2, ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2), NOW());

INSERT INTO participant_curriculum_item (is_checked, is_deleted, participant_id, curriculum_item_id, created_at, updated_at)
VALUES
    (true, false, 1, 1, CURDATE(), NOW()),
    (true, false, 1, 2, CURDATE(), NOW()),
    (false, false, 1, 1,CURDATE(), NOW()),
    (true, true, 1, 1,CURDATE(), NOW()),
    (true, false, 1, 1, ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2), ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+3));
