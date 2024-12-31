<!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Confirmation de paiement</title>
    </head>
    <body style="line-height: 1.8">
        <p>Bonjour <i>${name}</i>,</p>
        <p>Nous vous remercions pour la confiance que vous nous accordez.</p>
        <p>Les détails du paiement sont les suivants :</p>
        <ul>
            <li>Montant Total : <b><i>${amount} FCFA</i></b></li>
            <li>Caution : <i>${caution} FCFA</i></li>
            <li>Frais de reparation : <i>${repair} FCFA</i></li>
            <li>Loyer mensuel : <i>${rent} FCFA</i></li>
            <li>Nombre de mois : <i>${month}</i></li>
            <li>Mode de paiement : <i>${mode}</i></li>
        </ul>
        <p>Merci,</p>
        <p>L'équipe support</p>
    </body>
</html>