import java.io.*;
import java.util.Scanner;
// обьявление класса, он реализовывает интерфейс Serializable
public class Car implements Serializable {
    // обявление скрытых полей класса
    private int id; // id
    private String model; // модель
    private String manufacturer; // производитель
    private double price; // стоимость
    private int count; // количество
    private static final long serialVersionUID = 8180497438563284099L;
    // конструктор по умоляанию
    public Car(){}
    // конструктор с параметрами
    public Car(String model,String manufacturer,double price,int count) throws Exception {
        if(price <=0 || count <=0) { // если цена и/или количество меньше или равно 0
            throw new Exception("Значение цены и/или количества меньше или равно нулю."); // выбирамываем исключение
        }
        else // иначе
        {
            // копируем значение полей из параметоров
            this.model = model;
            this.manufacturer = manufacturer;
            this.count=count;
            this.price=price;
        }
    }
    // метод для получения id
    public int getId() {
        return id;
    }

    // метод для установки значения id
    public void setId(int id) {
        this.id = id;
    }
    // статический метод для записи обьекта в текстовый файл
    public static void write(Car car, String filename)
    {
        try
        {
            // создание обьектов для записи
            FileOutputStream fos=new FileOutputStream(filename);
            PrintStream printStream = new PrintStream(fos);
            // запись полей в файл с новой строки каждый
            printStream.println(car.getModel());
            printStream.println(car.getManufacturer());
            printStream.println(car.getPrice());
            printStream.println(car.getCount());
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    // статический метод для чтения обьекта из текстового файла
    public static Car read(String filename) throws FileNotFoundException {
        Car car = new Car();
        try {
            // создание обьектов для чтения
            FileInputStream fis = new FileInputStream(filename);
            Scanner sc = new Scanner(fis);
            // чтение полей по одному из строки
            car.setModel(sc.nextLine());
            car.setManufacturer(sc.nextLine());
            car.setPrice(Double.valueOf(sc.nextLine()));
            car.setCount(Integer.valueOf(sc.nextLine()));
            sc.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return car; // возврат обьекта
    }
    // методы для изменения и получения значений полей класса
    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // перегруженный метод toString, возращает строку со значениями полей обьекта класса, удобный для вывода данных
    @Override
    public String toString() {
        return String.format(manufacturer + "\t\t\t" + model + "\t\t\t" + price + "\t\t\t" + count);
    }
    @Override
    public boolean equals(Object obj) {
        Car car = (Car) obj;
        if(price != car.price || count != car.count || !model.equals(car.model)
                || !manufacturer.equals(car.manufacturer))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}