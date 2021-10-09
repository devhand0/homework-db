//        Все должно быть сделано внутри программы на JAVA в качестве sql сервера использовать MySQL
//
//        Создать таблицу Student
//        Колонки id, fio, sex, id_group
//
//        Создать таблицу Group
//        Колонки id, name, id_curator
//
//        Создать таблицу Curator
//        Колонки id, fio
//
//        Заполнить таблицы данными(15 студентов, 3 группы, 4 куратора)
//
//        Вывести на экран информацию о всех студентах включая название группы и имя куратора
//
//        Вывести на экран количество студентов
//
//        Вывести студенток
//
//        Обновить данные по группе сменив куратора
//
//        Вывести список групп с их кураторами
//
//        Используя вложенные запросы вывести на экран студентов из определенной группы(поиск по имени группы)
//
//        Критерии оценки:
//        Проходной балл - 9 баллов.
//
//        За каждый пункт 1 балл.
//
//        Рекомендуем сдать до: 07.10.2021
//        Статус: не сдано

package hw;

import com.github.javafaker.Faker;

import java.sql.*;

public class HomeworkPreparation {


    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    //очистка и удаление перед запуском
    private static final String DROP_CURATOR_TABLE = "DROP TABLE IF EXISTS public.Curator CASCADE;";
    private static final String DROP_GROUP_TABLE = "DROP TABLE IF EXISTS public.Group CASCADE;";
    private static final String DROP_GENDER_TYPE = "DROP TYPE IF EXISTS gender CASCADE;";
    private static final String DROP_STUDENT_TABLE = "DROP TABLE IF EXISTS public.Student CASCADE;";

    public static void printResult(ResultSet var) throws SQLException {
        ResultSetMetaData rsMd = var.getMetaData();
        int cCount = rsMd.getColumnCount();
        while (var.next()){
            String row = "";
            for (int i = 1; i <= cCount; i++){
                row += var.getString(i) + " \t | \t ";
            }
            System.out.println(row);
        }
    }


    public void cleanTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.execute(DROP_CURATOR_TABLE);
            statement.execute(DROP_GROUP_TABLE);
            statement.execute(DROP_GENDER_TYPE);
            statement.execute(DROP_STUDENT_TABLE);

        }
    }

    //Запросы на создание табиц
    private static final String CR_CURATOR_TABLE = "CREATE TABLE IF NOT EXISTS public.Curator(id bigserial primary key, fio varchar(50));";
    private static final String CR_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS public.Group(id bigserial primary key, name varchar(10), id_curator bigserial, FOREIGN KEY(id_curator) REFERENCES Curator(id));";
    private static final String CR_GENDER_TYPE = "CREATE TYPE gender as enum ('m', 'f');";
    private static final String CR_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS public.Student(id bigserial primary key, fio varchar(50), sex gender NOT NULL, id_group bigserial, FOREIGN KEY(id_group) REFERENCES public.group(id));";
//создание табиц
    public void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.execute(CR_CURATOR_TABLE);
            statement.execute(CR_GROUP_TABLE);
            statement.execute(CR_GENDER_TYPE);
            statement.execute(CR_STUDENT_TABLE);

        }
    }
    //Заполнение таблицы куратор
    public void insertTableCurator(Connection connection) throws SQLException {
        for(int i = 1; i < 5; i++){
            Faker faker = new Faker();
            String name = (faker.name().lastName() + " " + faker.name().firstName()).replace("'", "l");
            String QUERY = ("insert into public.Curator (id,fio)values(" + i + "," + "'"+name+"'" + ");");
            System.out.println(QUERY);
            try (Statement statement = connection.createStatement()) {
                statement.execute(QUERY);
            }
        }
    }
    //Заполнение таблицы группы
    public void insertTableGroup(Connection connection) throws SQLException {
        int rMin = 1;
        int rMax = 4;
        int cRand = rMin + (int)(Math.random() * rMax);
        String QUERY_1 = ("insert into public.Group (id,name,id_curator)values(1,'IT',1);");
        System.out.println(QUERY_1);
        int cRand2 = rMin + (int)(Math.random() * rMax);
        String QUERY_2 = ("insert into public.Group (id,name,id_curator)values(2,'FINANCE',2);");
        System.out.println(QUERY_2);
        int cRand3 = rMin + (int)(Math.random() * rMax);
        String QUERY_3 = ("insert into public.Group (id,name,id_curator)values(3,'MANAGE',3);");
        System.out.println(QUERY_3);
            try (Statement statement = connection.createStatement()) {
                statement.execute(QUERY_1);
                statement.execute(QUERY_2);
                statement.execute(QUERY_3);
            }
    }
    //Заполнение таблицы студенты
    public void insertTableStudent(Connection connection) throws SQLException {
        int rMin = 1;
        int rMax = 3;

        for(int i = 1; i < 16; i++){
            String gEn;
//            gEn = "";
            Faker faker = new Faker();
            String name = (faker.name().lastName() + " " + faker.name().firstName()).replace("'","l");
            int gRand = rMin + (int)(Math.random() * rMax);
            int sRand = rMin + (int)(Math.random() * rMax);
            if (sRand % 2 == 0) {
                gEn = "m";
            } else {
                gEn = "f";
            }
            String QUERY = ("insert into public.student (id,fio,sex,id_group)values(" + i + "," + "'" + name + "'" + "," + "'" + gEn + "'" +  "," + gRand + ")");
            System.out.println(QUERY);
            try (Statement statement = connection.createStatement()) {
                statement.execute(QUERY);
            }
            }
        }

    public void SelectTable(Connection connection) throws SQLException {
        //SELECT'Ы
//        Вывести на экран информацию о всех студентах включая название группы и имя куратора
        final String selectALL = "select s.id, s.fio, s.sex, g.name, c.fio from public.student s, public.group g, public.curator c where s.id_group=g.id AND g.id_curator=c.id ;";
//        Вывести на экран количество студентов
        final String selectALLCount = "select MAX(id) FROM  public.student;";
//        Вывести студенток
        final String selectFem = "select s.id, s.fio, s.sex from public.student s where s.sex='f';";
//        Вывести список групп с их кураторами
        final String selectGroup = "SELECT g.name, c.fio FROM public.group g LEFT JOIN public.curator c ON g.id_curator = c.id;";
//        Используя вложенные запросы вывести на экран студентов из определенной группы(поиск по имени группы)
        final String selectMemberOfIT = "select s.id, s.fio, s.sex from public.student s where s.id_group = (select g.id from public.group g where g.name='IT' );";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSetall = statement.executeQuery(selectALL);
            System.out.println("Список всех");
            printResult(resultSetall);
            ResultSet resultSetCount = statement.executeQuery(selectALLCount);
            System.out.println("Количество всех");
            printResult(resultSetCount);
            ResultSet resultSetFem = statement.executeQuery(selectFem);
            System.out.println("Список всех Девушек");
            printResult(resultSetFem);
            ResultSet resultSetGroup = statement.executeQuery(selectGroup);
            System.out.println("Список всех групп");
            printResult(resultSetGroup);
            ResultSet resultSetMemberOfIT = statement.executeQuery(selectMemberOfIT);
            System.out.println("Список всех из групп IT");
            printResult(resultSetMemberOfIT);
        }
    }
    public void UpdateTable(Connection connection) throws SQLException {
        final String updateTableGroup = "update public.group set id_curator=4 where id_curator=1;";
        final String selectGroup = "SELECT g.name, c.fio FROM public.group g LEFT JOIN public.curator c ON g.id_curator = c.id;";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(updateTableGroup);
            ResultSet resultSetGroup = statement.executeQuery(selectGroup);
            System.out.println("Список всех групп !после смены куратора!");
            printResult(resultSetGroup);
        }

    }
        public static void main(String[] args) throws SQLException {
        HomeworkPreparation homeworkPreparation = new HomeworkPreparation();
        try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            homeworkPreparation.cleanTables(connection);
            homeworkPreparation.createTables(connection);
            homeworkPreparation.insertTableCurator(connection);
            homeworkPreparation.insertTableGroup(connection);
            homeworkPreparation.insertTableStudent(connection);
            homeworkPreparation.SelectTable(connection);
            homeworkPreparation.UpdateTable(connection);
        }
    }

}
