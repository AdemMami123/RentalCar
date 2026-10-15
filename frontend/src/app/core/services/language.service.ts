import { DOCUMENT } from '@angular/common';
import { Injectable, Inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type LanguageCode = 'en' | 'fr' | 'ar';

const translations: Record<LanguageCode, Record<string, string>> = {
  en: {
    'language.label': 'Language',
    'language.en': 'English',
    'language.fr': 'Français',
    'language.ar': 'العربية',
    'nav.explore': 'Explore cars',
    'nav.howItWorks': 'How it works',
    'nav.dashboard': 'Dashboard',
    'nav.manageCars': 'Manage cars',
    'nav.manageBookings': 'Manage bookings',
    'nav.myBookings': 'My bookings',
    'nav.signedInAs': 'Signed in as',
    'nav.logout': 'Log out',
    'nav.signIn': 'Sign in',
    'nav.createAccount': 'Create account',
    'nav.toggle': 'Toggle navigation',
    'fleet.eyebrow': 'The fleet',
    'fleet.title': 'Find your next ride.',
    'fleet.subtitle': 'Choose a car that fits the route, the mood, and the people coming along.',
    'fleet.filters': 'Fleet filters',
    'fleet.search': 'Search make or model',
    'fleet.allTypes': 'All types',
    'fleet.loading': 'Loading the fleet...',
    'fleet.noMatch': 'No cars match your search.',
    'fleet.carsInFleet': 'cars in fleet',
    'fleet.seats': 'seats',
    'fleet.viewCar': 'View car',
    'fleet.defaultDescription': 'A well-kept car ready for your next journey.',
    'carType.SEDAN': 'Sedan',
    'carType.SUV': 'SUV',
    'carType.HATCHBACK': 'Hatchback',
    'carType.COUPE': 'Coupe',
    'carType.CONVERTIBLE': 'Convertible',
    'status.AVAILABLE': 'Available',
    'status.UNAVAILABLE': 'Unavailable',
    'car.back': 'Back to fleet',
    'car.loading': 'Loading car details...',
    'car.favorite': 'Save to favorites',
    'car.removeFavorite': 'Remove from favorites',
    'car.vehicleInfo': 'Vehicle Information',
    'car.make': 'Make:',
    'car.model': 'Model:',
    'car.year': 'Year:',
    'car.registration': 'Registration Number:',
    'car.dailyRate': 'Daily Rate:',
    'car.status': 'Status:',
    'car.unavailable': 'Unavailable',
    'booking.title': 'Book this vehicle',
    'booking.pickupDate': 'Pick-up Date *',
    'booking.dropoffDate': 'Drop-off Date *',
    'booking.pickupLocation': 'Pick-up Location *',
    'booking.dropoffLocation': 'Drop-off Location *',
    'booking.selectPickup': 'Select pick-up location',
    'booking.selectDropoff': 'Select drop-off location',
    'booking.specialRequests': 'Special Requests',
    'booking.placeholder': 'Any special requests or requirements...',
    'booking.checking': 'Checking availability...',
    'booking.available': '✓ Available',
    'booking.noneAvailable': '✗ No cars available',
    'booking.availableForDates': 'cars available for these dates.',
    'booking.tryDates': 'Try these available dates:',
    'booking.duration': 'Rental Duration:',
    'booking.day': 'day(s)',
    'booking.total': 'Total Price:',
    'booking.required': 'This field is required',
    'booking.bookNow': 'Book Now',
    'booking.booking': 'Booking...'
  },
  fr: {
    'language.label': 'Langue',
    'language.en': 'English',
    'language.fr': 'Français',
    'language.ar': 'العربية',
    'nav.explore': 'Voir les voitures',
    'nav.howItWorks': 'Comment ça marche',
    'nav.dashboard': 'Tableau de bord',
    'nav.manageCars': 'Gérer les voitures',
    'nav.manageBookings': 'Gérer les réservations',
    'nav.myBookings': 'Mes réservations',
    'nav.signedInAs': 'Connecté en tant que',
    'nav.logout': 'Se déconnecter',
    'nav.signIn': 'Se connecter',
    'nav.createAccount': 'Créer un compte',
    'nav.toggle': 'Ouvrir la navigation',
    'fleet.eyebrow': 'La flotte',
    'fleet.title': 'Trouvez votre prochaine voiture.',
    'fleet.subtitle': 'Choisissez une voiture adaptée à votre trajet et à vos envies.',
    'fleet.filters': 'Filtres de la flotte',
    'fleet.search': 'Rechercher une marque ou un modèle',
    'fleet.allTypes': 'Tous les types',
    'fleet.loading': 'Chargement de la flotte...',
    'fleet.noMatch': 'Aucune voiture ne correspond à votre recherche.',
    'fleet.carsInFleet': 'voitures dans la flotte',
    'fleet.seats': 'places',
    'fleet.viewCar': 'Voir la voiture',
    'fleet.defaultDescription': 'Une voiture bien entretenue, prête pour votre prochain trajet.',
    'carType.SEDAN': 'Berline',
    'carType.SUV': 'SUV',
    'carType.HATCHBACK': 'Citadine',
    'carType.COUPE': 'Coupé',
    'carType.CONVERTIBLE': 'Cabriolet',
    'status.AVAILABLE': 'Disponible',
    'status.UNAVAILABLE': 'Indisponible',
    'car.back': 'Retour à la flotte',
    'car.loading': 'Chargement des détails...',
    'car.favorite': 'Ajouter aux favoris',
    'car.removeFavorite': 'Retirer des favoris',
    'car.vehicleInfo': 'Informations du véhicule',
    'car.make': 'Marque :',
    'car.model': 'Modèle :',
    'car.year': 'Année :',
    'car.registration': 'Immatriculation :',
    'car.dailyRate': 'Tarif journalier :',
    'car.status': 'Statut :',
    'car.unavailable': 'Indisponible',
    'booking.title': 'Réserver ce véhicule',
    'booking.pickupDate': 'Date de départ *',
    'booking.dropoffDate': 'Date de retour *',
    'booking.pickupLocation': 'Lieu de départ *',
    'booking.dropoffLocation': 'Lieu de retour *',
    'booking.selectPickup': 'Sélectionner le lieu de départ',
    'booking.selectDropoff': 'Sélectionner le lieu de retour',
    'booking.specialRequests': 'Demandes particulières',
    'booking.placeholder': 'Demandes ou besoins particuliers...',
    'booking.checking': 'Vérification de la disponibilité...',
    'booking.available': '✓ Disponible',
    'booking.noneAvailable': '✗ Aucune voiture disponible',
    'booking.availableForDates': 'voitures disponibles pour ces dates.',
    'booking.tryDates': 'Essayez ces dates disponibles :',
    'booking.duration': 'Durée de location :',
    'booking.day': 'jour(s)',
    'booking.total': 'Prix total :',
    'booking.required': 'Ce champ est obligatoire',
    'booking.bookNow': 'Réserver',
    'booking.booking': 'Réservation...'
  },
  ar: {
    'language.label': 'اللغة',
    'language.en': 'English',
    'language.fr': 'Français',
    'language.ar': 'العربية',
    'nav.explore': 'استكشف السيارات',
    'nav.howItWorks': 'كيف يعمل',
    'nav.dashboard': 'لوحة التحكم',
    'nav.manageCars': 'إدارة السيارات',
    'nav.manageBookings': 'إدارة الحجوزات',
    'nav.myBookings': 'حجوزاتي',
    'nav.signedInAs': 'تم تسجيل الدخول باسم',
    'nav.logout': 'تسجيل الخروج',
    'nav.signIn': 'تسجيل الدخول',
    'nav.createAccount': 'إنشاء حساب',
    'nav.toggle': 'تبديل التنقل',
    'fleet.eyebrow': 'الأسطول',
    'fleet.title': 'اعثر على رحلتك القادمة.',
    'fleet.subtitle': 'اختر السيارة المناسبة لطريقك ومزاجك ومن يرافقك.',
    'fleet.filters': 'فلاتر الأسطول',
    'fleet.search': 'ابحث عن الماركة أو الطراز',
    'fleet.allTypes': 'كل الأنواع',
    'fleet.loading': 'جار تحميل الأسطول...',
    'fleet.noMatch': 'لا توجد سيارات تطابق بحثك.',
    'fleet.carsInFleet': 'سيارات في الأسطول',
    'fleet.seats': 'مقاعد',
    'fleet.viewCar': 'عرض السيارة',
    'fleet.defaultDescription': 'سيارة بحالة ممتازة جاهزة لرحلتك القادمة.',
    'carType.SEDAN': 'سيدان',
    'carType.SUV': 'دفع رباعي',
    'carType.HATCHBACK': 'هاتشباك',
    'carType.COUPE': 'كوبيه',
    'carType.CONVERTIBLE': 'مكشوفة',
    'status.AVAILABLE': 'متاحة',
    'status.UNAVAILABLE': 'غير متاحة',
    'car.back': 'العودة إلى الأسطول',
    'car.loading': 'جار تحميل تفاصيل السيارة...',
    'car.favorite': 'حفظ في المفضلة',
    'car.removeFavorite': 'إزالة من المفضلة',
    'car.vehicleInfo': 'معلومات السيارة',
    'car.make': 'الماركة:',
    'car.model': 'الطراز:',
    'car.year': 'السنة:',
    'car.registration': 'رقم التسجيل:',
    'car.dailyRate': 'السعر اليومي:',
    'car.status': 'الحالة:',
    'car.unavailable': 'غير متاحة',
    'booking.title': 'احجز هذه السيارة',
    'booking.pickupDate': 'تاريخ الاستلام *',
    'booking.dropoffDate': 'تاريخ التسليم *',
    'booking.pickupLocation': 'مكان الاستلام *',
    'booking.dropoffLocation': 'مكان التسليم *',
    'booking.selectPickup': 'اختر مكان الاستلام',
    'booking.selectDropoff': 'اختر مكان التسليم',
    'booking.specialRequests': 'طلبات خاصة',
    'booking.placeholder': 'أي طلبات أو احتياجات خاصة...',
    'booking.checking': 'جار التحقق من التوفر...',
    'booking.available': '✓ متاحة',
    'booking.noneAvailable': '✗ لا توجد سيارات متاحة',
    'booking.availableForDates': 'سيارات متاحة لهذه التواريخ.',
    'booking.tryDates': 'جرّب هذه التواريخ المتاحة:',
    'booking.duration': 'مدة الإيجار:',
    'booking.day': 'يوم',
    'booking.total': 'السعر الإجمالي:',
    'booking.required': 'هذا الحقل مطلوب',
    'booking.bookNow': 'احجز الآن',
    'booking.booking': 'جار الحجز...'
  }
};

@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly storageKey = 'rentalcar-language';
  private readonly languageSubject = new BehaviorSubject<LanguageCode>(this.getInitialLanguage());
  readonly language$ = this.languageSubject.asObservable();

  constructor(@Inject(DOCUMENT) private readonly document: Document) {
    this.applyDirection(this.languageSubject.value);
  }

  get language(): LanguageCode { return this.languageSubject.value; }

  setLanguage(language: LanguageCode): void {
    if (!translations[language]) return;
    localStorage.setItem(this.storageKey, language);
    this.languageSubject.next(language);
    this.applyDirection(language);
  }

  translate(key: string): string {
    const value = translations[this.language][key] ?? translations.en[key];
    if (value) return value;
    const label = key.split('.').pop() ?? key;
    return label.replace(/[_-]+/g, ' ').toLowerCase().replace(/^\w/, character => character.toUpperCase());
  }

  private getInitialLanguage(): LanguageCode {
    const stored = localStorage.getItem(this.storageKey);
    if (stored && stored in translations) return stored as LanguageCode;
    const browserLanguage = (navigator.language || '').slice(0, 2) as LanguageCode;
    return browserLanguage in translations ? browserLanguage : 'fr';
  }

  private applyDirection(language: LanguageCode): void {
    this.document.documentElement.lang = language;
    this.document.documentElement.dir = language === 'ar' ? 'rtl' : 'ltr';
  }
}
