SET SEARCH_PATH = PUBLIC;





insert into  chat_user values (1, '2024-11-07'::timestamp, '1998-02-28', 'ntmquan282@gmail.com', 'Quan Nguyen', '$2a$10$aA733lP0UyVykk2mthONVuB3f5yiagZwNA7KsfNSUi0g9JcnSdgIK', null,'0379309318');
insert into  chat_user values (2, '2024-11-07'::timestamp, '1998-02-28', 'ntmquan2822@gmail.com', 'Quan Nguyen2', '$2a$10$aA733lP0UyVykk2mthONVuB3f5yiagZwNA7KsfNSUi0g9JcnSdgIK', null,'03793093182');
insert into  chat_user values (3, '2024-11-07'::timestamp, '1998-02-28', 'ntmquan2823@gmail.com', 'Software Architecture', '$2a$10$aA733lP0UyVykk2mthONVuB3f5yiagZwNA7KsfNSUi0g9JcnSdgIK', null,'03793093183');
insert into  chat_user values (4, '2024-11-07'::timestamp, '1998-02-28', 'ntmquan2824@gmail.com', 'Software Architecture2', '$2a$10$aA733lP0UyVykk2mthONVuB3f5yiagZwNA7KsfNSUi0g9JcnSdgIK', null,'03793093184');
insert into  chat_user values (5, '2024-11-07'::timestamp, '1998-02-28', 'ntmquan2825@gmail.com', 'Software Architecture2', '$2a$10$aA733lP0UyVykk2mthONVuB3f5yiagZwNA7KsfNSUi0g9JcnSdgIK', null,'03793093185');

----id|creation_time|status|updated_by|friend_id|requested_user
insert into friend_ship values (1, '2024-07-07'::timestamp, 'ACTIVE', 2, 1);
insert into friend_ship values (2, '2024-07-07'::timestamp, 'ACTIVE', 3, 1);
----id|creation_time|admin|last_message_time|is_private_chat|status
insert into conversation values (1, '2027-07-07'::timestamp, 1, '2024-11-11'::timestamp, false, 'ACTIVE');
insert into conversation values (2, '2027-07-07'::timestamp, 1, '2024-11-11'::timestamp, true, 'ACTIVE');
insert into conversation values (3, '2027-07-07'::timestamp, 1, '2024-11-11'::timestamp, true, 'ACTIVE');

--id|creation_time|conversation_display_name|last_view|conversation_id|user_id
insert into participant values (1, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 1, 1);
insert into participant values (2, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 1, 2);
insert into participant values (3, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 1, 3);
insert into participant values (4, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 2, 1);
insert into participant values (5, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 2, 4);
insert into participant values (6, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 3, 1);
insert into participant values (7, '2024-07-07'::timestamp, 'conv1', '2024-11-11 10:00:00'::timestamp, 3, 3);
--id|creation_time|content|content_type|sender|conversation_id
--insert into chat_message values (1, '2024-07-07'::timestamp, 'test message1', 'TEXT', 1, 1);
--insert into chat_message values (2, '2024-07-07'::timestamp, 'test message1', 'TEXT', 2, 1);
--insert into chat_message values (3, '2024-07-07'::timestamp, 'test message1', 'TEXT', 3, 1);

SELECT setval('conversation_id_seq', 4, true);
SELECT setval('chat_user_id_seq', 6, true);
SELECT setval('friend_ship_id_seq', 3, true);
SELECT setval('participant_id_seq', 8, true);


