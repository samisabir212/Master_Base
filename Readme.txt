this master project extends other projects
each Application project shall have its own util package with all necessary utility classes
	seleniumHelper
	Lib_Fred
	ExcelUtil
	WebEventListener
this master class will contain only the WebEventListener and ExtentReporterNG classes

all Launch type configurations are parametrized in the TestNG xml file

other configurations such as OR and Config are located in the master Properties Config package