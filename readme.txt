
Tema 2 APD - Detectia documentelor similare folosind Replicated Workers

Budau Alexandru 333CB

clasa principala : MapReduce

	metode: 
	readInput - citirea dateleor din fisierul de input transmis ca al doilea parametru

	MapReduceOperation - realizarea operatiei de map-reduce pe un singur fisier dat ca parametru, pe baza numarului de threaduri si a dimensiunii fragmentului de parcurs pentru fiecare thread in parte

	CompareOperation - realizarea operatiei de comparare a fisierelor precizate in fisierul de input

	writeOutput - scrierea dateleor in fisierul de output transmis ca al treilea parametru

Structuri ajutatoare (clase):
FileStat 
		index fisier 
		un obiect de tip File
		numarul de cuvinte 
		lista de asocieri cuvinte - numar de aparitii (HASH MAP)

metoda MapReduceOperation
	--OPERATIA MAP--
	-creaza un WorkPool cu elemente de tipul MapPartialSolution
	-se creaza o prima solutie partiala ce va fi adaugata in workpool (un work item cu fragmentul incepand de la pozitia 0 si terminandu-se dupa D octeti specificati in fisierul de input)
	-se creaza o lista de Worker-i de tipul MapWorker, numarul de workeri va fi egal cu nuamrul de thread-uri specificate la apelul programului - NT (argumentul 1)
	-se pornesc threadurile de tip worker -> start, dupa care se asteapta finalizarea lor -> join
	-se salveaza rezultatele obtinute de fecare worker -> hashlist-urile pentru fragmentele analizate -> se vor stoca intr-un ArrayList care va fi pasat la operatia reduce

	--OPERATIA REDUCE--
	-creaza un WorkPool cu elemente de tipul ReducePartialSolution
	-se creaza o prima solutie partiala ce va fi adaugata in workpool (se trimite ca parametru intreaga lista de hashmapuri)
	-se creaza o lista de Worker-i de tipul ReduceWorker, numarul de workeri va fi egal cu nuamrul de thread-uri specificate la apelul programului - NT (argumentul 1)
	-se pornesc threadurile de tip worker -> start, dupa care se asteapta finalizarea lor -> join
	-rezultatul se salveaza intr-un obiect de tip fileStat ce va fi adaugat intr-o lista utilizata la operatia de Compare

metoda CompareOperation
	-creaza un WorkPool cu elemente de tipul ComparePartialSolution
	-se creaza o prima solutie partiala ce va fi adaugata in workpool (se trimit primele doua fisiere ce vor fi comparate)
	-se creaza o lista de Worker-i de tipul CompareWorker, numarul de workeri va fi egal cu nuamrul de thread-uri specificate la apelul programului - NT (argumentul 1)
	-se pornesc threadurile de tip worker -> start, dupa care se asteapta finalizarea lor -> join
	-vectorul de perechi de fisiere ce se va gasi in clasa CompareWorker va fi sortat in functie de graadul de similaritate

clasa abstracta PartialSolution
	-o clasa de referinta pentru Worker 
	-va fi derivata in functie de worker
	-contine doar stari

clasa abstracta generica Worker este extinsa din Thread
	-metoda run: extrage din workpool un PartialSolution si il proceseaza -> processPartialSolution
	-metoda processPartialSolution este abstracta si va fi implementata de clasele derivate
	-accepta ca tip generic un obiect derivat din PartialSolution

clasa MapWorker derivata din Worker cu tipul MapPartialSolution
	-contine un hashMap
	-contine un obiect de tipul File
	-un obiect de tipul RandomAccesFile 
	-tipul MapPartialSolution:
			-contine o variabila de offset si una pentru FragmentSise
			-o solutie partiala primeste un fisier care va fi preluat de obiectul de tip RandomAccesFile, acesta va seta cursorul in dreptul offsetului si va parcurge fragmentul pana ce se va depasi FragmentSize octeti
	-metoda processPartialSoution:
		-va crea o noua solutie partiala ce va avea un offset deplasat cu offset+FragmentSize iar astfel un alt worker va prelucra un alt fragment
		-pentru fragmentul curent va citi cuvintele despartite prin separatori si se vor adauga in hashmap sau se vor incrementa valorile asociate lor
		-readString() : citirea unui cuvant se face cu ajutorul metode read() din RandomAccesFile care citeste un singur octet,
		-inainte de inceperea citirii se verifica daca caracterul imediat anterior offsetului a fost caracter sau nu; daca da acel cuvant va fi omis
		-daca capul de citire nu ajunge la finalul fragmentului metoda readSting() de citire cuvant poate citi cuvantul intreg depasind valoarea de final de fragment

		Obs: un worker poate analiza mai multe fragmente in mod aleatoriu, iar asocierile cuvant - numar de aparitii pot fi obtinute diferit la fiecare rulare
		Obs: operatia de mapare este paralelizata pentru NT threaduri


clasa ReduceWorker derivata din Worker cu tipul ReducePartialSolution
	-contine o lista de hashMap-uri -> rezultatele obtinute de workeri
	-tipul ReducePartialSolution:
			-contine o lista de HashMap-uri asocieri cuvant - numar de aparitii
			-aceasta lista va avea mereu doua elemente pentru comparat
	-metoda processPartialSoution:
		-va seta un lock pe lista de mai sus si va extrage primele doua hash-mapuri
		-se va crea o noua solutie partiala ce va contine lista fara cele doua elemente
		-se elimina lockul
		-cele doua hashmapuri vor fi reunite: se parcurge un hashmap , se verifica daca se gasesc cuvintele in al doilea hashmap; daca da se aduna valorile , daca nu se adauga
		-se seteaza un lock pe lista de mai sus iar hashmap-ul rezultat in urma reuniunii va fi adaugat in lista
		-se elimina lock-ul
		-la final se va gasi in lista doar un sngur hashmap -> return pentru toti workerii

		Obs: operatia de reducere este paralelizata pentru NT threaduri

structuri ajutatoare:
clasa Comparison: contine doua obiecte de tipu FileStat si gradul de similaritate => simRate

clasa CompareWorker derivata din Workercu tipul ComparePartialSolution
	-contine un vector PAIRS de elemente de tipul Comparison
	-tipul ComparePartialSolution:
			-contine un vector de obiecte de tipul FileStat
			-contine pozitiile obiectelor care vor fi comparate in vectorul de mai sus
	-metoda processPartialSoution:
		-se va crea o noua solutie partiala ce va contine vectorul de mai sus si indecsi diferiti pentru a lasa un alt worker sa compare alte doua fisiere
		-pentru soluta partiala curenta se va calcula gradul de similaritate conform formulei prezentate
		-se seteaza un lock pe vectorul PAIRS si adauga un nou element reprezentand comparatia intre cele doua obiecte 





