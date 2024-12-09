<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title></title>
</head>
<body>
    <p>Bonjour <i>${name}</i>,</p>
    <p>Le paiement que vous avez initié a été annulé pour le motif <i>${motif}</i>. </p>
    <p>Détail du paiement :</p>
    <ul>
        <li>Montant : <i>${amount}</i></li>
        <li>Loyer : <i>${rent}</i></li>
        <li>Nombre de mois : <i>${month}</i></li>
        <li>Caution : <i>${caution}</i></li>
        <li>Frais de reparation : <i>${repair}</i></li>
        <li>Mode de paiement : <i>${mode}</i></li>
    </ul>
    <p>Merci,</p>
    <p>L'équipe support</p>
</body>
</html>