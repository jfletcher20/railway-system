
/*
 * Na željezničkoj infrastrukturi potrebno je obavljati radove na prugama i njihovim 
kolosijecima kao preventivno održavanje ili za otklanjanje kvarova. Radovi se u pravilu ne 
obavljaju na cijeloj pruzi nego se obavljaju između dviju stanica zbog čega treba pruzi na toj 
relaciji pruge (između dviju stanica) promijeniti status. Statusi pruge su: I - ispravna, K – u kvaru, 
T – u testiranju, Z - zatvorena. Ako na određenoj relaciji pruge (između dviju stanica) postoji samo 
jedan kolosijek tada promjena statusa obuhvaća oba smjera (normalni i obrnuti). Npr. na pruzi 
M501 za stanice Donji Mihaljevec – Čehovec radi se o normalnom smjeru, a Čehovec - Donji 
Mihaljevec o obrnutom smjeru. Ako se mijenja status pruge koji označava da je u kvaru to znači 
da vlakovi ne mogu putovati tom prugom između tih dviju stanica jer postoji samo jedan kolosijek. 
Ako je na nekoj pruzi između dviju stanica postavljen status pruge da je zatvorena tada ta 
relacija mora proći testiranje da bi mogla dobila status da je ispravna. Na istoj pruzi moguće su 
promjene statusa pruge između više relacija (između dviju stanica), ali relacije ne mogu biti u 
presjeku. Npr. na pruzi M501 može se za relaciju između stanica Donji Mihaljevec - Čehovec 
postaviti status da je u kvaru. Zatim se može na istoj pruzi M501 za relaciju između stanica 
Čehovec – Mala Subotica postaviti status da je u kvaru. No ne može se na istoj pruzi M501 za 
relaciju između stanica Donji Kraljevec – Mala Subotica postaviti status da je u kvaru jer se stanica 
Donji Kraljevec nalazi na istoj pruzi između stanica Donji Mihaljevec  i Čehovec za koju je već 
postavljeno da je u kvaru. Logično je da se ne može na istoj pruzi M501 za relaciju između stanica 
Mala Subotica - Donji Kraljevec postaviti status da je u kvaru jer se stanica Donji Kraljevec nalazi 
između stanica Donji Mihaljevec  i Čehovec za čiju je relaciju već postavljeno da je u kvaru, a 
pruga na tom dijelu ima samo jedan kolosijek.  
Ako na određenoj relaciji pruge (između dviju stanica) postoje dva kolosijeka tada 
promjena statusa obuhvaća samo onaj smjer (normalni ili obrnuti) koji je određen redoslijedom 
stanica. To znači da vlakovi mogu putovati tom prugom između tih dviju stanica u suprotnom 
smjeru. Npr. ako je na pruzi M101 za relaciju između stanica Podsused – Gajnice postavljan 
status da je u kvaru to znači da je pruga u kvaru za tu relacija u normalnom smjeru, ali je pruga 
na istoj relaciji u obrnutom smjeru (Gajnice – Podsused) i daje ispravna. Moguće je na pruzi M101 
za relaciju između stanica Vrapče - Zaprešić postaviti status da je u kvaru to znači da je pruga u 
kvaru za tu relacija u obrnutom smjeru pruge. U ovom slučaju postoji dozvoljeni presjek jer se 
relacija Gajnice – Podsused nalazi unutar relacije Zaprešić – Vrapče, ali je relacija Vrapče – 
Zaprešić u obrnutom smjeru. S druge strane, kada se gleda funkcioniranje sustava ovo je slučaj 
kada prugom M101 ne bi mogli voziti vlakovi u presjeku relacija za oba smjera jer su kvarovi na 
oba kolosijeka, što je u ovom slučaju relacija Podsused – Gajnice za normalni smjer i relacija 
Gajnice – Podsused za obrnuti smjer. 
Rad s prugama i njihovim statusima treba se temeljiti na uzorku dizajna State. Promjena 
statusa pruge kada nije ispravna utječe na vozni red, a time i na kupovinu karata. 
 */

package edu.unizg.foi.uzdiz.jfletcher20.interfaces;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackSegment;

public interface ITrainTrackSegmentState {
    public TrainTrackStatus internalState();
    public boolean setState(TrainTrackSegment segment, ITrainTrackSegmentState state);
}