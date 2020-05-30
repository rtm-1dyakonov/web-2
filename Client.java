public class Client {
    // обявление скрытых полей класса
    private int id; // id
    private String name; // имя
    private String login; // логин
    private String password; // пароль
    private Rental []list; // массив списков обьектов
    private int length; // размер массива
    private int maxSize; // максимальное количество обьектов в массиве

    // конструтор класса с одним параметром
    public Client(int maxSize){
        length = 0;
        this.maxSize = maxSize;
        list = new Rental[maxSize];
    }
    // конструтор класса с несколькими параметрами
    public Client(String name, String login, String password, int maxSize)
    {
        this.name =  name;
        this.login = login;
        this.password = password;
        length = 0;
        this.maxSize = maxSize;
        list = new Rental[maxSize];
    }

    // метод вывод названий всех пунктов проката клиента
    public void print()
    {
        for (int i = 0; i < length; i++) {
            System.out.println((i + 1) + "\t" + list[i].getPoint());
        }
    }

    public void setRentalId(int i, int id)
    {
        try {
            list[i].setId(id); // установка значения id
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // выброс исключения
            throw new ArrayIndexOutOfBoundsException("Индекс за пределами массива списков обьектов.");
        }
    }

    // метод добавления точки проката в массив
    public void addListItem(Rental rental)
    {
        if(maxSize > length) // если количество элементов в массиве меньше чем его длина
        {
            list[length] = rental; // добавление элемента в конец массива
            length++; // увеличение количества элементов в массиве
        }
        else // иначе
        {
            // выброс исключения об ошибке
            throw new ArrayIndexOutOfBoundsException("Выход за пределы массива списков обьектов при попытке добавления нового обьекта.");
        }
    }

    // метод для удаления точки проката по индексу i с массива
    public void deleteListItem(int i) {
        try {
            for(int index = i; index < length - 1; i++) {
                list[index] = list[index + 1]; // сдвиг элементов на один влево, таким образом затирается нужный элемент в массиве
            }
            length--; // уменьшение количества элементов в массиве
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // выброс исключения
            throw new ArrayIndexOutOfBoundsException("Индекс за пределами массива списков обьектов.");
        }
    }

    // метод для получения обьекта точки проката по индексу в массиве
    public Rental getListItem(int i) {
        try {
            return list[i]; // возврат обьекта по индексу
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // выброс исключения
            throw new ArrayIndexOutOfBoundsException("Индекс за пределами массива списков обьектов.");
        }
    }

    // методы для получения значений полей
    public int getLength() {
        return length;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    // методы для установки значений полей
    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
