const suma = require('./node_modules/2025a-swgr1-mmb-suma');
const resta = require('./node_modules/2025a-swgr1-mmb-resta');
const multiplicacion = require('./node_modules/2025a-swgr1-mmb-multiplicacion');
const division = require('./node_modules/2025a-swgr1-mmb-divison');

function calcular() {
  console.log('Calculadora Básica');
  console.log('------------------');
  console.log('suma(2,1):', suma.suma(2,1)); 
  console.log('resta(3,2):', resta.resta(3,2)); 
  console.log('multiplicacion(2,5):', multiplicacion.multiplicacion(2,5)); 
  console.log('division(9,3):', division.division(9,3));
}

calcular();